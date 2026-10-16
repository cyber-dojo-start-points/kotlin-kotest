# Kotest specs are discovered by scanning the compiled classes. Selecting by
# package finds the engine but none of its tests, so the classpath is scanned.

JARS=`ls /kotlin/*.jar | tr '\n' ':'`
CLASSES=.:${JARS}
LAUNCHER=`ls /kotlin/junit-platform-console-standalone-*.jar`

# The compiler is started directly rather than through the kotlinc script. That
# script runs the compiler behind a Preloader, which loads it through a
# classloader of its own, and an AOT cache recorded for that arrangement is
# slower than no cache at all. Started this way the compiler's classes come off
# the classpath, where the cache can reach them.
COMPILER_JAR=/usr/share/kotlin/kotlinc/lib/kotlin-compiler.jar
COMPILER_MAIN=org.jetbrains.kotlin.cli.jvm.K2JVMCompiler

# Both JVMs below read something the image recorded at build time instead of
# doing the same work on every run. Nothing is lost when either is missing or
# unusable: the JVM says so and does the work itself, and the run is slower.
#
# The collector is named rather than left to the JVM to choose. Replaying the
# AOT cache under the one it picks by default kills the compiler outright with
# SIGILL, five runs in six, and a compiler that dies before printing anything
# leaves no summary line, so every kata scores amber whatever its tests did.
# A JVM that lives for half a second has nothing to gain from a concurrent
# collector in any case.
COMPILER_OPTS=()
COMPILER_OPTS+=(-XX:AOTCache=/kotlin/kotlinc.aot)          # the compiler's own classes, linked
COMPILER_OPTS+=(-XX:+UseSerialGC)                          # see above; the default one crashes replaying the cache
COMPILER_OPTS+=(-XX:TieredStopAtLevel=1)                   # still ahead even over a half-second compile
COMPILER_OPTS+=(-XX:CICompilerCount=2)                     # leaves cores for the test JVM warming up beside it
COMPILER_OPTS+=(-XX:+UnlockExperimentalVMOptions)          # for the next flag
COMPILER_OPTS+=(-XX:+AOTCompileEagerly)                    # compiles what the cache recorded as hot, at once
COMPILER_OPTS+=(-Xms256m -Xmn200m)                         # room to compile without collecting
COMPILER_OPTS+=(-XX:InitialCodeCacheSize=128m)             # otherwise a full collection runs to make room
COMPILER_OPTS+=(-XX:ReservedCodeCacheSize=240m)            # for the initial size above
COMPILER_OPTS+=(-Xlog:cds*=off)                            # a JVM that cannot use it says so on stdout
COMPILER_OPTS+=(-Xlog:aot*=off)                            # as above, for the aot cache
COMPILER_OPTS+=(--enable-native-access=ALL-UNNAMED)        # the kotlinc script passes this on jdk 24+
COMPILER_OPTS+=(--sun-misc-unsafe-memory-access=allow)     # as above

# The tests run in a JVM started before the compiler, which gets the test
# engine and mockk going while the kata compiles, and then waits to be told
# whether there is anything to test. That takes about 0.5s off every run.
# /kotlin/kata-tests/KataTests.java in the image is what it runs.
TEST_OPTS=()
TEST_OPTS+=(-XX:TieredStopAtLevel=1)                       # as for the compiler
TEST_OPTS+=(-XX:AOTCache=/kotlin/junit.aot)                # recorded with the module option below
TEST_OPTS+=(-Xlog:aot*=off)                                # a JVM that cannot use it says so on stdout
TEST_OPTS+=(--enable-final-field-mutation=ALL-UNNAMED)     # the console launcher mutates one, and warns
TEST_OPTS+=(-Dkata.tests.warm.up=/kotlin/kata-tests/warm-up)  # the kata it warms up on
# mockk attaches byte-buddy's agent part way through the run. Allowed to
# attach to itself, the JVM does it in-process. Otherwise byte-buddy starts a
# third JVM just to attach, which costs about 0.07s. Do not swap this for
# -javaagent: an agent named at startup turns the whole AOT cache off.
TEST_OPTS+=(-Djdk.attach.allowAttachSelf=true)
TEST_OPTS+=(-XX:+EnableDynamicAgentLoading)                # and says nothing about it
TEST_OPTS+=(-XX:-PrintWarnings)                            # nor that the agent stops some class sharing, nor any other JVM warning
# The kata's own directory is on the classpath, after the jars, because kotest
# looks for a project config, io.kotest.provided.ProjectConfig, there.
TEST_CLASSPATH=/kotlin/kata-tests/kata-tests.jar:${LAUNCHER}:${JARS}.

# The compiler targets 1.8 unless told otherwise, and mockk's every, verify
# and the rest are inline functions built for 11, which it then refuses to
# inline. 17 is named rather than the JDK's own version because the image
# takes the newest JDK, and a target the compiler does not yet know is an
# error; bytecode for 17 runs on anything newer.
readonly JVM_TARGET=17

# By default you get a _large_ stack-trace when tests fail. The frames naming
# the test engine and the coroutines it runs tests on are dropped, so the
# failing line in your own file survives whatever package you put it in.
#
# grep reports whether it matched, not whether the run it filters failed, so
# the status of the tests themselves has to come from PIPESTATUS.
function run_tests()
{
  java "${TEST_OPTS[@]}" -cp "${TEST_CLASSPATH}" KataTests \
    execute \
    --class-path . \
    --scan-classpath . \
    --details=summary \
    --disable-ansi-colors \
    2>&1 | grep -Ev '^[[:space:]]*(io\.kotest|kotlinx?\.coroutines)\.'
  return ${PIPESTATUS[0]}
}

# The test JVM reads one line from fd 4, and runs the tests once that line is
# "go". The pipe exists before the test JVM starts, so neither end can be
# opened too late.
exec 4> >(run_tests)
readonly TESTS=$!

# Every .kt file is compiled, including ones in sub-directories and ones
# nothing else refers to yet, so a file you are midway through writing shows
# its errors instead of being silently skipped.
#
# The compiler loads its scripting plugin, for .kts files, unless told not to.
java "${COMPILER_OPTS[@]}" -cp "${COMPILER_JAR}" "${COMPILER_MAIN}" \
  -jvm-target ${JVM_TARGET} \
  -Xdisable-default-scripting-plugin \
  `find . -name '*.kt'` -cp $CLASSES -d . 4>&-
compiled=$?
if [ $compiled -ne 0 ]; then
  # Killed rather than told to stop, because a JVM told to stop while mockk
  # is attaching its agent can hang instead of ending.
  pkill -KILL -P ${TESTS}
  exec 4>&-
  wait ${TESTS}
  exit $compiled
fi

# A test JVM that has already died has nobody reading fd 4, and writing to it
# would end this script with SIGPIPE. Ignoring that leaves the status the
# test JVM ended with, and whatever it said, to report what went wrong.
( trap '' PIPE; echo go >&4 ) 2> /dev/null
exec 4>&-
wait ${TESTS}

package dojo /* see comment at top of cyber-dojo.sh */

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot

class HikerTest : StringSpec() {

    init {
        "top level function" {
            val logger = mockk<Logger>(relaxed = true)
            answer(logger) shouldBe 42
            verify(exactly = 1) { logger.log("the answer is 42") }
        }

        "class method" {
            val logger = mockk<Logger>(relaxed = true)
            Hiker(logger).instanceAnswer() shouldBe 42
            verify(exactly = 1) { logger.log("the answer is 42") }
        }

        // Example test showing mockk's vocabulary. It asserts on values of
        // its own rather than on the code being written, so it goes on
        // passing while that is rewritten. It counts as a test, so the
        // totals include it.
        "mockk vocabulary" {
            // mockk() answers only the calls that every {} sets up, and
            // throws on any other. relaxed = true answers the rest too, with
            // a default value, which is what lets a logger be ignored.
            val strict = mockk<Logger>()
            every { strict.log("hello") } returns Unit
            strict.log("hello")

            // returns answers a call. mockk<T>() on an interface with a
            // return value needs every {} before that call is made.
            val calculator = mockk<Calculator>()
            every { calculator.times(6, 7) } returns 42
            calculator.times(6, 7) shouldBe 42

            // verify checks a call happened, exactly = n how often.
            // verify(exactly = 0) checks one never did.
            val counter = mockk<Counter>(relaxed = true)
            counter.tick()
            counter.tick()
            verify(exactly = 2) { counter.tick() }
            verify(exactly = 0) { counter.tock() }

            // throws makes the call raise instead of answering
            val broken = mockk<Calculator>()
            every { broken.times(any(), any()) } throws RuntimeException("no answer")
            shouldThrow<RuntimeException> { broken.times(1, 2) }

            // any() matches any argument, and slot captures what was passed
            val captured = slot<String>()
            val logger = mockk<Logger>(relaxed = true)
            logger.log("the answer is 42")
            verify { logger.log(capture(captured)) }
            captured.captured shouldBe "the answer is 42"
        }
    }
}

interface Calculator {
    fun times(a: Int, b: Int): Int
}

interface Counter {
    fun tick()
    fun tock()
}

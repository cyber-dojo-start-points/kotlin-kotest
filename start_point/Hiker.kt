package dojo

// The starting files are unrelated to the exercise.
//
// They simply show syntax for writing and testing
//  o) a top-level function, which logs the answer it returns
//  o) a class method, which delegates to the top-level function
// Pick the style that best fits the exercise.
// Then delete the other one, along with this comment!

interface Logger {
    fun log(message: String)
}

fun answer(logger: Logger): Int {
    val answer = 6 * 9
    logger.log("the answer is $answer")
    return answer
}

class Hiker(private val logger: Logger) {

    fun instanceAnswer(): Int {
        return answer(logger)
    }
}

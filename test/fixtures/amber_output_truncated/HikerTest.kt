package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HikerTest : StringSpec({
    // The test passes, and prints so much that the runner keeps only the
    // head of the stream. The summary the colour rests on is past that,
    // so it never reaches the rag-lambda.
    "life the universe and everything" {
        for (i in 0 until 20000) {
            println("debug: i is $i")
        }
        answer() shouldBe 42
    }
})

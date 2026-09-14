package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HikerTest : StringSpec({
    // Throws, so it is an error rather than a failed assertion.
    "life the universe and everything" {
        val zero = "0".toInt()
        (answer() / zero) shouldBe 42
    }

    // A failed assertion, alongside the error above.
    "the answer is three digits long" {
        answer().toString().length shouldBe 3
    }
})

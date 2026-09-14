package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HikerTest : StringSpec({
    "life the universe and everything" {
        // The compiler cannot fold this, so the division happens at run time.
        val zero = "0".toInt()
        (answer() / zero) shouldBe 42
    }

    // The test above throws. This one still runs, and passes.
    "the answer is even" {
        (answer() % 2) shouldBe 0
    }
})

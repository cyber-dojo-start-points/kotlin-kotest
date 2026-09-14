package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HikerTest : StringSpec({
    "life the universe and everything" {
        // The compiler cannot fold this, so the loop is never entered at
        // compile time and the test spins until the runner stops it.
        while ("0".toInt() == 0) {
        }
        answer() shouldBe 42
    }
})

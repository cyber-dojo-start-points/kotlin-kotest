package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HikerTest : StringSpec({
    "life the universe and everything" {
        answer() shouldBe 42
    }

    "the answer is two digits long" {
        digitCount(answer()) shouldBe 2
    }
})

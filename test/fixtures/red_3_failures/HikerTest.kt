package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HikerTest : StringSpec({
    "life the universe and everything" {
        answer() shouldBe 42
    }

    "the answer is odd" {
        (answer() % 2) shouldBe 1
    }

    "the hiker is called arthur" {
        Hiker().name shouldBe "arthur"
    }
})

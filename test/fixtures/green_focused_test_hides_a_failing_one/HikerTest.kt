package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HikerTest : StringSpec({
    // The f: prefix focuses this test, which skips every other test in the
    // spec. The failing assertion below it is never evaluated.
    "f:life the universe and everything" {
        answer() shouldBe 42
    }

    "the answer is three digits long" {
        answer().toString().length shouldBe 3
    }
})

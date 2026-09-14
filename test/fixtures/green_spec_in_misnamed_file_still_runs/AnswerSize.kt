package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AnswerSizeTest : StringSpec({
    "the answer is two digits long" {
        answer().toString().length shouldBe 2
    }
})

package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AnswerSizeTest : StringSpec({
    "the answer is three digits long" {
        answer().toString().length shouldBe 3
    }
})

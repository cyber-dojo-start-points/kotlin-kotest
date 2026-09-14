package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FizzBuzzTest : StringSpec({
    "three is fizz" {
        fizzBuzz(3) shouldBe "fizz"
    }
})

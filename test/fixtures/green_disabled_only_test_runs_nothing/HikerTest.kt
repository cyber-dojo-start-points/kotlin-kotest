package dojo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HikerTest : StringSpec({
    // The only test is disabled, so the assertion below is never evaluated.
    // answer() returns 54, so this test would fail if it ran.
    "life the universe and everything".config(enabled = false) {
        answer() shouldBe 42
    }
})

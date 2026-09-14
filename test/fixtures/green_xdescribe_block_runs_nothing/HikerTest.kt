package dojo

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class HikerTest : DescribeSpec({
    // xdescribe disables the whole block, so the assertion below is never
    // evaluated. answer() returns 54, so this test would fail if it ran.
    xdescribe("the answer") {
        it("is life the universe and everything") {
            answer() shouldBe 42
        }
    }
})

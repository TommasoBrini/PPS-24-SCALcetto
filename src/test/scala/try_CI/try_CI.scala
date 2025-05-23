package try_CI

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class try_CI extends AnyFlatSpec with Matchers:

  "A try_CI" should "work correctly" in:
    1 should be(2)

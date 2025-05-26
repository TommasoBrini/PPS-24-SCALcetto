package try_CI

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Test_Try_CI extends AnyFlatSpec with Matchers:

  val tr_CI: Try_CI = Try_CI_Impl

  "A try_CI" should "work correctly" in:
    tr_CI.n() should be(1)
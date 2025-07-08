package dsl.creation.build

import dsl.creation.CreationSyntax.{ball, player, team}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.matchers.should.Matchers

final class TestMatchBuilder extends AnyFlatSpec with Matchers {
  "MatchBuilder" should "fail when the number of teams is not exactly two" in {
    val mb = MatchBuilder()
    an[IllegalArgumentException] should be thrownBy mb.build()
  }
}

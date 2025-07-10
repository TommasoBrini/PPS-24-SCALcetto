package dsl.creation.build

import dsl.creation.CreationSyntax.{ball, player, team}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.matchers.should.Matchers
import model.Match.Score

final class MatchBuilderSpec extends AnyFlatSpec with Matchers {
  "MatchBuilder" should "fail when the number of teams is not exactly two" in {
    val mb = MatchBuilder(Score.init())
    an[IllegalArgumentException] should be thrownBy mb.build()
  }
}

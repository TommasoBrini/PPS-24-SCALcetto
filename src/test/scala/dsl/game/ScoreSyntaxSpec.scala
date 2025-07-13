package dsl.game

import dsl.game.ScoreSyntax.*
import model.Match.Score
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoreSyntaxSpec extends AnyFlatSpec with Matchers:

  "westGoal" should "increment only the west team’s score" in:
    val initial = Score(2, 2)
    val updated = initial.westGoal

    updated.westScore shouldBe 3
    updated.eastScore shouldBe 2

  it should "leave the original Score unchanged (immutability)" in:
    val initial = Score(0, 0)
    val _       = initial.westGoal
    initial shouldBe Score(0, 0)

  "eastGoal" should "increment only the east team’s score" in:
    val initial = Score(4, 1)
    val updated = initial.eastGoal

    updated.westScore shouldBe 4
    updated.eastScore shouldBe 2

  it should "work correctly when both scores are already high" in:
    val initial = Score(10, 10)
    val updated = initial.eastGoal

    updated shouldBe Score(10, 11)

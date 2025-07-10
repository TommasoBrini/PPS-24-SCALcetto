package dsl

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import model.Match.*
import model.Space.*
import dsl.MatchSyntax.*
import config.UIConfig

class MatchSyntaxSpec extends AnyFlatSpec with Matchers:

  "isBallOut" should "be true if the ball is out" in:
    val state = Match((Team(Nil), Team(Nil)), Ball(Position(UIConfig.fieldWidth + 1, UIConfig.fieldHeight + 1)))
    state.isBallOut should be(true)

  it should "be false when ball is in game" in:
    val state = Match((Team(Nil), Team(Nil)), Ball(Position(UIConfig.fieldWidth / 2, UIConfig.fieldHeight / 2)))
    state.isBallOut should be(false)

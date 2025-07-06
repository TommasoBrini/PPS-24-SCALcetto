package dsl.game

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Ball, Player, Team}
import model.Space.Bounce.*
import dsl.game.PlayerSyntax.*
import model.decisions.PlayerRoleFactory.*
import model.Match.Decision
import model.Match.Action

import scala.util.Random

class TestPlayerSyntax extends AnyFlatSpec with Matchers:
  "A Player" should "correctly report hasBall when carrying a ball" in:
    val ball: Ball = Ball(Position(5, 5), Movement(Direction.none, 0))
    val playerWithBall =
      Player(1, Position(0, 0), Movement.still, Some(ball), Decision.Initial, Action.Initial).asBallCarrierPlayer
    val playerWithoutBall =
      Player(2, Position(0, 0), Movement.still, None, Decision.Initial, Action.Initial).asOpponentPlayer

    playerWithBall.ball.isDefined shouldBe playerWithBall.hasBall
    playerWithoutBall.ball.isDefined shouldBe playerWithoutBall.hasBall

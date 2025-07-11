package update

import config.{MatchConfig, UIConfig}
import dsl.SpaceSyntax.*
import model.Match.*
import model.Match.Action.*
import update.act.Act.*
import dsl.MatchSyntax.players
import Side.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ActSpec extends AnyFlatSpec with Matchers:
  val defaultSpeed                = 1
  val defaultDirection: Direction = Direction(1, 1)

  "An act phase" should "update player movement and position if it is moving" in:
    val initialPosition = Position(0, 0)
    val player =
      Player(0, initialPosition, movement = Movement.still, nextAction = Move(defaultDirection, defaultSpeed))
    val teamA        = Team(List(player), East)
    val teamB        = Team(List(player), West)
    val state        = Match((teamA, teamB), Ball(Position(0, 0)))
    val (updated, _) = actStep.run(state)
    updated.teams.players
      .forall(_.movement == Movement(defaultDirection, defaultSpeed)) should be(true)
    updated.teams.players
      .forall(_.position == initialPosition + Movement(defaultDirection, defaultSpeed)) should be(true)

  it should "update ball movement if someone hits it" in:
    val ball         = Ball(Position(0, 0), movement = Movement.still)
    val player       = Player(0, Position(0, 0), ball = Some(ball), nextAction = Hit(defaultDirection, defaultSpeed))
    val team         = Team(List(player))
    val state        = Match((team, team), ball)
    val (updated, _) = actStep.run(state)
    updated.ball.movement should be(Movement(defaultDirection, defaultSpeed))

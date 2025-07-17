package update

import dsl.MatchSyntax.players
import model.Match.*
import model.Match.Action.{Move, Stopped, Take}
import model.Match.Decision.{Intercept, Tackle}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import update.act.Act.*

class ActSpec extends AnyFlatSpec with Matchers:

  "An act phase" should "tackle ball carrier if someone successfully tackled him" in:
    val ball     = Ball(Position(0, 0))
    val carrier  = Player(0, Position(0, 0), ball = Some(ball))
    val tackling = Player(1, Position(0, 0), decision = Tackle(ball), nextAction = Take(ball))
    val state    = MatchState((Team(List(carrier)), Team(List(tackling))), ball)
    actStep.run(state)._1.players.find(_.id == carrier.id) match
      case Some(tackled) =>
        tackled.nextAction should matchPattern { case Stopped(_) => }
        tackled.ball should be(None)
      case None => fail("Tackled player not found")

  it should "update ball possession if some is taking the ball" in:
    val ball         = Ball(Position(0, 0))
    val player       = Player(1, Position(0, 0), ball = None, decision = Intercept(ball), nextAction = Take(ball))
    val team         = Team(List(player))
    val tuple        = (team, Team(Nil))
    val state        = MatchState(tuple, ball)
    val (updated, _) = actStep.run(state)
    updated.players.head.ball should be(Some(ball))
    updated.teams._1.hasBall should be(true)

  it should "update player and ball movement and position if they are moving" in:
    val ball         = Ball(Position(0, 0))
    val carrier      = Player(1, Position(0, 0), ball = Some(ball), nextAction = Move(Direction(1, 1), 1))
    val state        = MatchState((Team(List(carrier)), Team(Nil)), ball)
    val (updated, _) = actStep.run(state)

    updated.ball.movement shouldNot be(ball.movement)
    updated.players.head.movement shouldNot be(carrier.movement)
    updated.ball.position shouldNot be(ball.position)
    updated.players.head.position shouldNot be(carrier.position)

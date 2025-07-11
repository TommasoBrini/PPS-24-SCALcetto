package update

import model.Space.*
import model.Match.{Action, Decision, Score, Side}
import Action.Initial
import Decision.*
import Side.*
import dsl.creation.CreationSyntax.*
import update.validate.Validate.validateStep
import dsl.MatchSyntax.players

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ValidateSpec extends AnyFlatSpec with Matchers:

  "A validate phase" should "update every player action" in:
    val state = newMatch(Score.init()):
      team(West):
        player(0) decidedTo Run(Direction(1, 1), 1) isGoingTo Action.Initial
      team(East):
        player(1) decidedTo MoveToBall(Direction(1, 1)) isGoingTo Action.Initial
    val (updated, _) = validateStep.run(state)
    updated.players.forall(_.nextAction != Action.Initial) should be(true)

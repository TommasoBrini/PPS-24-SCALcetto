package update.act

import dsl.MatchSyntax.*
import dsl.action.ActionProcessor.*
import model.Match.{Action, Decision, Match}
import monads.States.State
import update.Update.Event

object Act:
  def actStep: State[Match, Option[Event]] =
    State(state => {
      val updatedState = state
        .mapIf(existsSuccessfulTackle)(_.tackleBallCarrier())
        .mapIf(isPossessionChanging)(_.updateBallPossession())
        .updateMovements()
        .moveEntities()
      (updatedState, updatedState.detectEvent())
    })

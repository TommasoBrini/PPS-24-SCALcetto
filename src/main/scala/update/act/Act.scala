package update.act

import dsl.MatchSyntax.*
import dsl.action.ActionProcessor.*
import model.Match.Match
import monads.States.State
import update.Update.Event

object Act:
  def actStep: State[Match, Option[Event]] =
    State(state => {
      val updatedState = state
        .applyIf(existsSuccessfulTackle)(_.tackleBallCarrier())
        .applyIf(isPossessionChanging)(_.updateBallPossession())
        .updateMovements()
        .moveEntities()
      (updatedState, updatedState.detectEvent())
    })

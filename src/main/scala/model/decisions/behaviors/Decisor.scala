package model.decisions.behaviors

import model.Match.*
import model.decisions.DecisorPlayer.*
import model.decisions.PlayerDecisionFactory.*
import config.UIConfig
import model.decisions.CommonPlayerDecisions.*
import config.Util
import config.MatchConfig
import model.decisions.behaviors.Rating.*

object Decisor:

  extension (player: Player)

    /** Decide the best action for the player based on the state of the match.
      *
      * @param matchState
      *   the current state of the match
      * @return
      *   the best action for the player
      */
    def decide(matchState: MatchState): Decision =
      player.calculateBestAction(matchState)

    private def calculateBestAction(state: MatchState): Decision =
      val possibleActions = player.possibleDecision(state)
      type Rating = Double
      val decisionRatings: Map[Decision, Rating] = possibleActions
        .map(decision => (decision, player.calculateActionRating(decision, state))).toMap
      decisionRatings.maxBy(_._2)._1

    private def possibleDecision(state: MatchState): List[Decision] =
      player match
        case c: ControlPlayer =>
          if c.decision == Decision.Initial
          then
            c.possiblePasses(state)
          else
            c.possibleShots(state) ++ c.possibleMoves(state) ++ c.possiblePasses(state) ++ c.possibleMovesToGoal(state)
        case _: OpponentPlayer => List(Decision.Confusion(30))
        case _: TeammatePlayer => List(Decision.Confusion(30))
        case _                 => throw new IllegalArgumentException("Unknown player type")

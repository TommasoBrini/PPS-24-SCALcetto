package model.decisions.behaviors

import model.Match.*
import model.decisions.DecisorPlayer.*
import model.decisions.PlayerDecisionFactory.*
import config.UIConfig
import model.decisions.CommonPlayerDecisions.*
import config.Util
import config.MatchConfig
import model.decisions.behaviors.Ratings.*

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
      val decisionRatings: Map[Decision, Double] = possibleActions.map { decision =>
        player match
          case c: ControlPlayer  => decision -> c.calculateActionRating(decision, state)
          case o: OpponentPlayer => decision -> o.calculateActionRating(decision, state)
          case t: TeammatePlayer => decision -> t.calculateActionRating(decision, state)
          case _                 => decision -> 0.0
      }.toMap
      decisionRatings.maxBy(_._2)._1

    private def possibleDecision(state: MatchState): List[Decision] =
      player match
        case c: ControlPlayer =>
          c.possibleMoves(state) ++ c.possiblePasses(state) ++ c.possibleShots(state) ++ c.possibleMovesToGoal(state)
        case o: OpponentPlayer =>
          o.possibleMarks(state) ++ o.possibleTackles(state) ++ o.possibleIntercepts(state) ++ o.possibleMoveToBall(
            state
          )
        case t: TeammatePlayer =>
          t.possibleMovesRandom(state) ++ t.possibleReceives(state)
        case _ => throw new IllegalArgumentException("Unknown player type")

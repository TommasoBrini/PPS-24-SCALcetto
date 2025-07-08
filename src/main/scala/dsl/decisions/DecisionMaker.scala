package dsl.decisions

import model.Match.*
import PlayerTypes.*
import PlayerRoleFactory.*
import config.UIConfig
import CommonPlayerDecisions.*
import config.Util
import config.MatchConfig
import dsl.decisions.behavior.{BallCarrierBehavior, OpponentBehavior, TeammateBehavior}

object DecisionMaker:

  extension (player: Player)
    /** Decide the best action for the player based on the state of the match.
      *
      * @param matchState
      *   the current state of the match
      * @return
      *   the best action for the player
      */
    def decide(matchState: Match, markings: Map[Player, Player]): Decision = player match
      case c: BallCarrierPlayer => BallCarrierBehavior.calculateBestDecision(c)(matchState)
      case o: OpponentPlayer    => OpponentBehavior.calculateBestDecision(o)(matchState, markings.get(player))
      case t: TeammatePlayer    => TeammateBehavior.calculateBestDecision(t)(matchState)
      case _                    => throw new IllegalArgumentException("Unknown player type")

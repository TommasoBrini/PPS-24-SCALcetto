package dsl.decisions

import model.Match.*
import PlayerTypes.*
import PlayerRoleFactory.*
import config.UIConfig
import CommonPlayerDecisions.*
import config.Util
import config.MatchConfig

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
      case c: BallCarrierPlayer =>
        import dsl.decisions.behavior.BallCarrierBehavior.*
        c.calculateBestDecision(matchState)
      case o: OpponentPlayer =>
        import dsl.decisions.behavior.OpponentBehavior.*
        o.calculateBestDecision(matchState, markings.get(player))
      case t: TeammatePlayer =>
        import dsl.decisions.behavior.TeammateBehavior.*
        t.calculateBestDecision(matchState)
      case _ => throw new IllegalArgumentException("Unknown player type")

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
    /** Determines the optimal action for a player based on their role and current match state.
      *
      * This method delegates decision-making to specialized behavior modules based on the player's role
      *
      * @param matchState
      *   The current state of the match
      * @param markings
      *   A mapping of defensive players to their assigned offensive targets
      * @return
      *   The best decision for the player given their role and current situation
      */
    def decide(matchState: MatchState, markings: Map[Player, Player]): Decision = player match
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

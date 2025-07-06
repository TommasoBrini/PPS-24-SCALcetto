package model.decisions

import model.Match.*
import model.decisions.DecisorPlayer.*
import model.decisions.PlayerDecisionFactory.*
import config.UIConfig
import model.decisions.CommonPlayerDecisions.*
import config.Util
import config.MatchConfig
import model.decisions.behavior.{ControlBehavior, OpponentBehavior, TeammateBehavior}

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
      case c: ControlPlayer  => ControlBehavior.calculateBestDecision(c)(matchState)
      case o: OpponentPlayer => OpponentBehavior.calculateBestDecision(o)(matchState, markings.get(player))
      case t: TeammatePlayer => TeammateBehavior.calculateBestDecision(t)(matchState)
      case _                 => throw new IllegalArgumentException("Unknown player type: " + player)

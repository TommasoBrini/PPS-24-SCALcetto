package model.decisions.behavior

import model.Match.*
import model.decisions.CommonPlayerDecisions.*
import model.decisions.PossibleDecisionFactory.*
import model.decisions.rating.ControlDecisionRating.*
import model.decisions.PlayerDecisionFactory.*
import model.decisions.DecisorPlayer.ControlPlayer

object ControlBehavior:
  extension (player: ControlPlayer)
    def calculateBestDecision(state: MatchState): Decision =
      val decisionRatings: Map[Decision, Double] = player.possibleDecisions(state)
        .map(decision => (decision, calculateActionRating(decision, player, state))).toMap
      decisionRatings.maxBy(_._2)._1

  private def calculateActionRating(playerDecision: Decision, player: Player, state: MatchState): Double =
    playerDecision match
      case run: Decision.Run               => run.rate(state)
      case pass: Decision.Pass             => pass.rate(state)
      case shoot: Decision.Shoot           => shoot.rate(state)
      case moveToGoal: Decision.MoveToGoal => moveToGoal.rate(player, state)
      case _                               => 0

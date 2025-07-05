package model.decisions.behavior

import model.Match.*
import model.decisions.PlayerTypes.*
import model.decisions.CommonPlayerDecisions.*
import model.decisions.DecisionGenerator.*
import model.decisions.rating.ControlDecisionRating.*
import model.decisions.PlayerRoleFactory.*

object ControlBehavior:
  extension (player: AttackingPlayer)
    def calculateBestDecision(state: MatchState): Decision =
      player.decision match
        case Decision.Run(direction, steps) if steps > 0 => player.decideRun(direction, steps - 1)
        case _ =>
          val possibleDecisions = player.generateAllPossibleDecisions(state)
          val decisionRatings: Map[Decision, Double] = possibleDecisions
            .map(decision => (decision, calculateActionRating(decision, player, state))).toMap
          decisionRatings.maxBy(_._2)._1

  private def calculateActionRating(playerDecision: Decision, player: Player, state: MatchState): Double =
    playerDecision match
      case run: Decision.Run               => run.rate(player, state)
      case pass: Decision.Pass             => pass.rate(state)
      case shoot: Decision.Shoot           => shoot.rate(state)
      case moveToGoal: Decision.MoveToGoal => moveToGoal.rate(player, state)
      case _                               => 0

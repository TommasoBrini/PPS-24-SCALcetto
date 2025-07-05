package model.decisions.behavior

import model.Match.*
import model.decisions.CommonPlayerDecisions.*
import model.decisions.PossibleDecisionFactory.*
import model.decisions.rating.ControlDecisionRating.*
import model.decisions.PlayerDecisionFactory.*
import model.decisions.DecisorPlayer.ControlPlayer

object ControlBehavior:
  extension (player: ControlPlayer)
    def calculateBestDecision(state: Match): Decision =
      player.decision match
        case Decision.Run(direction, steps) if steps > 0 => player.decideRun(direction, steps - 1)
        case _ =>
          val possibleDecisions = if player.decision == Decision.Initial then player.possiblePasses(state)
          else player.possibleDecisions(state)
          val decisionRatings: Map[Decision, Double] = possibleDecisions
            .map(decision => (decision, calculateActionRating(decision, player, state))).toMap
          decisionRatings.maxBy(_._2)._1

  private def calculateActionRating(playerDecision: Decision, player: Player, state: Match): Double =
    playerDecision match
      case run: Decision.Run               => run.rate(player, state)
      case pass: Decision.Pass             => pass.rate(state)
      case shoot: Decision.Shoot           => shoot.rate(state)
      case moveToGoal: Decision.MoveToGoal => moveToGoal.rate(player, state)
      case _                               => 0

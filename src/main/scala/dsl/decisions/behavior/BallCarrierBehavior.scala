package dsl.decisions.behavior

import model.Match.*
import dsl.decisions.PlayerTypes.*
import dsl.decisions.CommonPlayerDecisions.*
import dsl.decisions.DecisionGenerator.*
import dsl.decisions.rating.BallCarrierDecisionRating.*
import dsl.decisions.PlayerRoleFactory.*

object BallCarrierBehavior:

  extension (player: BallCarrierPlayer)
    /** Calculates the optimal decision for a ball carrier player based on current match state. Implements the
      * decision-making logic for players in possession of the ball.
      *
      * @param state
      *   The current match state
      * @return
      *   The best decision for the ball carrier player
      */
    def calculateBestDecision(state: MatchState): Decision =
      player.decision match
        case Decision.Run(direction, steps) if steps > 0 =>
          continueCurrentRun(player, direction, steps)
        case _ =>
          selectBestDecision(player, state)

  private def continueCurrentRun(player: Player, direction: Direction, remainingSteps: Int): Decision =
    player.createRunDecision(direction, remainingSteps - 1)

  private def selectBestDecision(player: BallCarrierPlayer, state: MatchState): Decision =
    val possibleDecisions = player.generateAllPossibleDecisions(state)
    val decisionRatings   = rateAllDecisions(possibleDecisions, player, state)
    selectHighestRatedDecision(decisionRatings)

  private def rateAllDecisions(decisions: List[Decision], player: Player, state: MatchState): Map[Decision, Double] =
    decisions.map(decision =>
      (decision, calculateDecisionRating(decision, player, state))
    ).toMap

  private def calculateDecisionRating(decision: Decision, player: Player, state: MatchState): Double =
    decision match
      case run: Decision.Run               => run.rate(player, state)
      case pass: Decision.Pass             => pass.rate(state)
      case shoot: Decision.Shoot           => shoot.rate(state)
      case moveToGoal: Decision.MoveToGoal => moveToGoal.rate(player, state)
      case _                               => 0.0

  private def selectHighestRatedDecision(decisionRatings: Map[Decision, Double]): Decision =
    decisionRatings.maxBy(_._2)._1

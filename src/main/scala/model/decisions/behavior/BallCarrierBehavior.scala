package model.decisions.behavior

import model.Match.*
import model.decisions.PlayerTypes.*
import model.decisions.CommonPlayerDecisions.*
import model.decisions.DecisionGenerator.*
import model.decisions.rating.BallCarrierDecisionRating.*
import model.decisions.PlayerRoleFactory.*

/** Behavior implementation for ball carrier players
  */
object BallCarrierBehavior:

  /** Calculates the best decision for an ball carrier player based on current match state
    *
    * @param player
    *   the ball carrier player making the decision
    * @param state
    *   the current match state
    * @return
    *   the best decision for the player
    */
  extension (player: BallCarrierPlayer)
    def calculateBestDecision(state: Match): Decision =
      player.decision match
        case Decision.Run(direction, steps) if steps > 0 =>
          continueCurrentRun(player, direction, steps)
        case _ =>
          selectBestDecision(player, state)

  /** Continues the current run decision by reducing the remaining steps
    */
  private def continueCurrentRun(player: Player, direction: Direction, remainingSteps: Int): Decision =
    player.createRunDecision(direction, remainingSteps - 1)

  /** Selects the decision with the highest rating
    */
  private def selectBestDecision(player: BallCarrierPlayer, state: Match): Decision =
    val possibleDecisions = player.generateAllPossibleDecisions(state)
    val decisionRatings   = rateAllDecisions(possibleDecisions, player, state)
    selectHighestRatedDecision(decisionRatings)

  /** Rates all possible decisions for the player
    *
    * Maps each decision to its rating using the appropriate rating function for the decision type.
    */
  private def rateAllDecisions(decisions: List[Decision], player: Player, state: Match): Map[Decision, Double] =
    decisions.map(decision =>
      (decision, calculateDecisionRating(decision, player, state))
    ).toMap

  /** Calculates the rating for a specific decision
    */
  private def calculateDecisionRating(decision: Decision, player: Player, state: Match): Double =
    decision match
      case run: Decision.Run               => run.rate(player, state)
      case pass: Decision.Pass             => pass.rate(state)
      case shoot: Decision.Shoot           => shoot.rate(state)
      case moveToGoal: Decision.MoveToGoal => moveToGoal.rate(player, state)
      case _                               => 0.0

  /** Selects the decision with the highest rating
    */
  private def selectHighestRatedDecision(decisionRatings: Map[Decision, Double]): Decision =
    decisionRatings.maxBy(_._2)._1

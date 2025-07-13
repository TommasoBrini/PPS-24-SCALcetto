package dsl.decisions.rating

import model.Match.*
import config.MatchConfig
import config.Util
import config.UIConfig
import dsl.space.PositionSyntax.*
import dsl.game.TeamsSyntax.*

/** Rating system for ball carrier player decisions.
  */
object BallCarrierDecisionRating:

  extension (run: Decision.Run)
    /** Rates a run decision based on path clarity and forward movement.
      *
      * @param player
      *   The player making the decision
      * @param state
      *   The current match state
      * @return
      *   Rating between 0.0 and 1.0
      */
    def rate(player: Player, state: Match): Double =
      if isRunDirectionClear(player, run.direction, state)
      then
        if isRunDirectionForward(player, run.direction, state)
        then RatingValues.VeryPoor
        else RatingValues.VeryVeryPoor
      else RatingValues.Impossible

  extension (pass: Decision.Pass)
    /** Rates a pass decision based on path clarity, distance, and advancement.
      *
      * @param state
      *   The current match state
      * @return
      *   Rating between 0.0 and 1.0
      */
    def rate(state: Match): Double =
      val passDetails = calculatePassDetails(pass, state)
      evaluatePassDecision(passDetails)

  extension (shoot: Decision.Shoot)
    /** Rates a shoot decision based on distance to goal and path clarity.
      *
      * @param state
      *   The current match state
      * @return
      *   Rating between 0.0 and 1.0
      */
    def rate(state: Match): Double =
      val shootDetails = calculateShootDetails(shoot, state)
      evaluateShootDecision(shootDetails)

  extension (moveToGoal: Decision.MoveToGoal)
    /** Rates a move-to-goal decision based on position and direction clarity.
      *
      * @param player
      *   The player making the decision
      * @param state
      *   The current match state
      * @return
      *   Rating between 0.0 and 1.0
      */
    def rate(player: Player, state: Match): Double =
      val moveDetails = calculateMoveToGoalDetails(moveToGoal, player, state)
      evaluateMoveToGoalDecision(moveDetails)

private object RatingValues:
  val Excellent: Double    = 1.0
  val Good: Double         = 0.8
  val Average: Double      = 0.7
  val Poor: Double         = 0.5
  val VeryPoor: Double     = 0.2
  val VeryVeryPoor: Double = 0.1
  val Impossible: Double   = 0.0

private object DistanceThresholds:
  val ShortPass: Int           = 30
  val MediumPass: Int          = 100
  val HighAdvancement: Int     = 50
  val ModerateAdvancement: Int = 20

private case class PassEvaluationDetails(pathClear: Boolean, advancement: Int, distance: Double)
private case class ShootEvaluationDetails(distance: Double, pathClear: Boolean)
private case class MoveToGoalEvaluationDetails(isOffensiveHalf: Boolean, directionClear: Boolean)

private def isRunDirectionClear(player: Player, direction: Direction, state: Match): Boolean =
  Util.isDirectionClear(player.position, direction, state)

private def calculatePassDetails(pass: Decision.Pass, state: Match): PassEvaluationDetails =
  val fromPosition = pass.from.position
  val toPosition   = pass.to.position
  val team         = determineTeam(pass.from, state)
  val pathClear    = Util.isPathClear(fromPosition, toPosition, state, team)
  val advancement  = calculateAdvancement(pass.from, fromPosition, toPosition, state)
  val distance     = fromPosition distanceFrom toPosition

  PassEvaluationDetails(pathClear, advancement, distance)

private def evaluatePassDecision(details: PassEvaluationDetails): Double =
  if !details.pathClear then
    RatingValues.Impossible
  else if details.distance < DistanceThresholds.ShortPass && details.advancement > DistanceThresholds.ModerateAdvancement
  then
    RatingValues.Excellent
  else if details.advancement > DistanceThresholds.HighAdvancement && details.distance < DistanceThresholds.MediumPass
  then
    RatingValues.Good
  else if details.advancement > 0 && details.distance < DistanceThresholds.MediumPass then
    RatingValues.Poor
  else if details.advancement > 0 then
    RatingValues.VeryPoor
  else
    RatingValues.Impossible

private def calculateShootDetails(shoot: Decision.Shoot, state: Match): ShootEvaluationDetails =
  val distance  = shoot.striker.position distanceFrom shoot.goal
  val team      = determineTeam(shoot.striker, state)
  val pathClear = Util.isPathClear(shoot.striker.position, shoot.goal, state, team)

  ShootEvaluationDetails(distance, pathClear)

private def evaluateShootDecision(details: ShootEvaluationDetails): Double =
  if details.distance > MatchConfig.highDistanceToGoal || !details.pathClear then
    RatingValues.Impossible
  else if details.distance <= MatchConfig.lowDistanceToGoal then
    RatingValues.Excellent
  else
    RatingValues.VeryPoor

private def calculateMoveToGoalDetails(
    moveToGoal: Decision.MoveToGoal,
    player: Player,
    state: Match
): MoveToGoalEvaluationDetails =
  val isTeamWest      = Util.isPlayerInWestTeam(player, state)
  val isOffensiveHalf = determineIfInOffensiveHalf(player, isTeamWest)
  val directionClear  = Util.isDirectionClear(player.position, moveToGoal.goalDirection, state)

  MoveToGoalEvaluationDetails(isOffensiveHalf, directionClear)

private def evaluateMoveToGoalDecision(details: MoveToGoalEvaluationDetails): Double =
  if !details.isOffensiveHalf then
    RatingValues.Impossible
  else if !details.directionClear then
    RatingValues.Impossible
  else
    RatingValues.Average

private def determineTeam(player: Player, state: Match): Team =
  state.teams.teamOf(player)

private def calculateAdvancement(passer: Player, from: Position, to: Position, state: Match): Int =
  if Util.isPlayerInWestTeam(passer, state) then
    to.x - from.x
  else
    from.x - to.x

private def determineIfInOffensiveHalf(player: Player, isTeamWest: Boolean): Boolean =
  if isTeamWest then
    player.position.x > UIConfig.fieldWidth / 2
  else
    player.position.x < UIConfig.fieldWidth / 2

private def isRunDirectionForward(player: Player, direction: Direction, state: Match): Boolean =
  if Util.isPlayerInWestTeam(player, state) then
    direction.x > 0
  else
    direction.x < 0

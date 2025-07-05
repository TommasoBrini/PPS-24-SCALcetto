package model.decisions.rating

import model.Match.*
import config.MatchConfig
import config.Util
import config.UIConfig

/** Rating system for attacking player decisions Provides evaluation methods for run, pass, shoot, and move-to-goal
  * decisions
  */
object AttackingDecisionRating:

  /** Rates a run decision based on path clarity
    * @param run
    *   the run decision to evaluate
    * @param player
    *   the player making the decision
    * @param state
    *   the current match state
    * @return
    *   rating between 0.0 and 1.0
    */
  extension (run: Decision.Run)
    def rate(player: Player, state: MatchState): Double =
      isRunDirectionClear(player, run.direction, state) match
        case true => RatingValues.VeryPoor
        case _    => RatingValues.Impossible

  /** Rates a pass decision based on path clarity, distance, and advancement
    * @param pass
    *   the pass decision to evaluate
    * @param state
    *   the current match state
    * @return
    *   rating between 0.0 and 1.0
    */
  extension (pass: Decision.Pass)
    def rate(state: MatchState): Double =
      val passDetails = calculatePassDetails(pass, state)
      evaluatePassDecision(passDetails)

  /** Rates a shoot decision based on distance to goal and path clarity
    * @param shoot
    *   the shoot decision to evaluate
    * @param state
    *   the current match state
    * @return
    *   rating between 0.0 and 1.0
    */
  extension (shoot: Decision.Shoot)
    def rate(state: MatchState): Double =
      val shootDetails = calculateShootDetails(shoot, state)
      evaluateShootDecision(shootDetails)

  /** Rates a move-to-goal decision based on position and direction clarity
    * @param moveToGoal
    *   the move-to-goal decision to evaluate
    * @param player
    *   the player making the decision
    * @param state
    *   the current match state
    * @return
    *   rating between 0.0 and 1.0
    */
  extension (moveToGoal: Decision.MoveToGoal)
    def rate(player: Player, state: MatchState): Double =
      val moveDetails = calculateMoveToGoalDetails(moveToGoal, player, state)
      evaluateMoveToGoalDecision(moveDetails)

/** Rating constants */
private object RatingValues:
  val Excellent: Double  = 1.0
  val Good: Double       = 0.8
  val Average: Double    = 0.7
  val Poor: Double       = 0.5
  val VeryPoor: Double   = 0.2
  val Impossible: Double = 0.0

/** Distance thresholds for decision evaluation */
private object DistanceThresholds:
  val ShortPass: Int           = 30
  val MediumPass: Int          = 100
  val HighAdvancement: Int     = 50
  val ModerateAdvancement: Int = 20

/** Case classes for evaluation details to improve code organization
  */
private case class PassEvaluationDetails(pathClear: Boolean, advancement: Int, distance: Double)
private case class ShootEvaluationDetails(distance: Double, pathClear: Boolean)
private case class MoveToGoalEvaluationDetails(isOffensiveHalf: Boolean, directionClear: Boolean)

/** Checks if the run direction is clear of obstacles
  */
private def isRunDirectionClear(player: Player, direction: Direction, state: MatchState): Boolean =
  Util.isDirectionClear(player.position, direction, state)

/** Calculates pass evaluation details
  */
private def calculatePassDetails(pass: Decision.Pass, state: MatchState): PassEvaluationDetails =
  val fromPosition = pass.from.position
  val toPosition   = pass.to.position
  val teamId       = determineTeamId(pass.from, state)
  val pathClear    = Util.isPathClear(fromPosition, toPosition, state, teamId)
  val advancement  = calculateAdvancement(pass.from, fromPosition, toPosition, state)
  val distance     = fromPosition.getDistance(toPosition)

  PassEvaluationDetails(pathClear, advancement, distance)

/** Evaluates pass decision based on calculated details
  */
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

/** Calculates shoot evaluation details
  */
private def calculateShootDetails(shoot: Decision.Shoot, state: MatchState): ShootEvaluationDetails =
  val distance  = shoot.striker.position.getDistance(shoot.goal)
  val teamId    = determineTeamId(shoot.striker, state)
  val pathClear = Util.isPathClear(shoot.striker.position, shoot.goal, state, teamId)

  ShootEvaluationDetails(distance, pathClear)

/** Evaluates shoot decision based on calculated details
  */
private def evaluateShootDecision(details: ShootEvaluationDetails): Double =
  if details.distance > MatchConfig.highDistanceToGoal || !details.pathClear then
    RatingValues.Impossible
  else if details.distance <= MatchConfig.lowDistanceToGoal then
    RatingValues.Excellent
  else
    RatingValues.VeryPoor

/** Calculates move-to-goal evaluation details
  */
private def calculateMoveToGoalDetails(
    moveToGoal: Decision.MoveToGoal,
    player: Player,
    state: MatchState
): MoveToGoalEvaluationDetails =
  val isTeamHead      = state.teams.head.players.contains(player)
  val isOffensiveHalf = determineIfInOffensiveHalf(player, isTeamHead)
  val directionClear  = Util.isDirectionClear(player.position, moveToGoal.goalDirection, state)

  MoveToGoalEvaluationDetails(isOffensiveHalf, directionClear)

/** Evaluates move-to-goal decision based on calculated details
  */
private def evaluateMoveToGoalDecision(details: MoveToGoalEvaluationDetails): Double =
  if !details.isOffensiveHalf then
    RatingValues.Impossible
  else if !details.directionClear then
    RatingValues.Impossible
  else
    RatingValues.Average

/** Determines the team ID for a player
  */
private def determineTeamId(player: Player, state: MatchState): Int =
  if state.teams.head.players.contains(player) then
    state.teams.head.id
  else
    state.teams.last.id

/** Calculates the advancement value for a pass
  */
private def calculateAdvancement(passer: Player, from: Position, to: Position, state: MatchState): Int =
  if state.teams.head.players.contains(passer) then
    to.x - from.x
  else
    from.x - to.x

/** Determines if a player is in the offensive half of the field
  */
private def determineIfInOffensiveHalf(player: Player, isTeamHead: Boolean): Boolean =
  if isTeamHead then
    player.position.x > UIConfig.fieldWidth / 2
  else
    player.position.x < UIConfig.fieldWidth / 2

package model.decisions

import model.Match.*
import config.UIConfig
import model.Space.*
import config.MatchConfig
import dsl.game.TeamsSyntax.*
import config.Util

/** Common decision-making capabilities for all players Provides basic movement and action decisions
  */
object CommonPlayerDecisions:

  extension (player: Player)

    /** Creates a run decision in the specified direction
      * @param direction
      *   the direction to run
      * @param steps
      *   the number of steps to run
      * @return
      *   a run decision
      */
    def createRunDecision(direction: Direction, steps: Int): Decision =
      Decision.Run(direction, steps)

    /** Generates all possible run directions for a player Uses for comprehension for clean functional composition
      * @param matchState
      *   the current match state
      * @return
      *   list of possible run decisions
      */
    def generatePossibleRunDirections(matchState: MatchState): List[Decision] =
      val validDirections =
        for
          dx <- -1 to 1
          dy <- -1 to 1
          if (dx == 0) != (dy == 0)
        yield player.createRunDecision(Direction(dx, dy), MatchConfig.runSteps)
      validDirections.toList

    /** Creates a confusion decision
      * @param remainingSteps
      *   the remaining confusion steps
      * @return
      *   a confusion decision
      */
    def createConfusionDecision(remainingSteps: Int): Decision =
      Decision.Confusion(remainingStep = remainingSteps)

    /** Creates a move to ball decision
      * @param directionToBall
      *   the direction towards the ball
      * @return
      *   a move to ball decision
      */
    def createMoveToBallDecision(directionToBall: Direction): Decision =
      Decision.MoveToBall(directionToBall)

/** Trait for players that can make passing decisions
  */
trait CanDecideToPass:
  self: Player =>

  /** Creates a pass decision to another player
    * @param target
    *   the target player
    * @return
    *   a pass decision
    */
  def createPassDecision(target: Player): Decision =
    Decision.Pass(this, target)

  /** Generates all possible pass decisions for available teammates Uses functional composition with filter and map
    * @param matchState
    *   the current match state
    * @return
    *   list of possible pass decisions
    */
  def generatePossiblePasses(matchState: MatchState): List[Decision] =
    for
      teammate <- matchState.teams.teamOf(this).players.filter(!_.equals(this))
    yield this.createPassDecision(teammate)

/** Trait for players that can make shooting decisions
  */
trait CanDecideToShoot:
  self: Player =>

  /** Creates a shoot decision towards a goal position
    * @param goalPosition
    *   the target goal position
    * @return
    *   a shoot decision
    */
  def createShootDecision(goalPosition: Position): Decision =
    Decision.Shoot(this, goalPosition)

  /** Generates all possible shoot decisions towards goal positions Uses functional composition to determine goal
    * positions
    * @param matchState
    *   the current match state
    * @return
    *   list of possible shoot decisions
    */
  def generatePossibleShots(matchState: MatchState): List[Decision] =
    val goalPositions = determineGoalPositions(matchState)
    goalPositions.map(createShootDecision)

  private def determineGoalPositions(matchState: MatchState): List[Position] =
    val goalX = determineGoalXCoordinate(matchState)
    List(
      Position(goalX, UIConfig.firstPoleY),
      Position(goalX, UIConfig.midGoalY),
      Position(goalX, UIConfig.secondPoleY)
    )

  private def determineGoalXCoordinate(matchState: MatchState): Int =
    if Util.isPlayerInFirstTeam(this, matchState) then UIConfig.goalEastX else UIConfig.goalWestX

/** Trait for players that can move towards the goal
  */
trait CanDecideToMoveToGoal:
  self: Player =>

  /** Creates a move to goal decision
    * @param direction
    *   the direction towards the goal
    * @return
    *   a move to goal decision
    */
  def createMoveToGoalDecision(direction: Direction): Decision =
    Decision.MoveToGoal(direction)

  /** Generates possible move to goal decisions
    * @param matchState
    *   the current match state
    * @return
    *   list of possible move to goal decisions
    */
  def generatePossibleMovesToGoal(matchState: MatchState): List[Decision] =
    val goalPosition    = determineGoalPosition(matchState)
    val directionToGoal = this.position.getDirection(goalPosition)
    List(createMoveToGoalDecision(directionToGoal))

  private def determineGoalPosition(matchState: MatchState): Position =
    if Util.isPlayerInFirstTeam(this, matchState) then
      Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
    else
      Position(0, UIConfig.fieldHeight / 2)

/** Trait for players that can mark opponents
  */
trait CanDecideToMark:
  self: Player =>

  /** Creates a mark decision
    * @param target
    *   the player to mark
    * @param teamId
    *   the team identifier
    * @return
    *   a mark decision
    */
  def createMarkDecision(target: Player, teamId: Int): Decision =
    Decision.Mark(this, target, teamId)

/** Trait for players that can tackle the ball
  */
trait CanDecideToTackle:
  self: Player =>

  /** Creates a tackle decision
    * @param ball
    *   the ball to tackle
    * @return
    *   a tackle decision
    */
  def createTackleDecision(ball: Ball): Decision =
    Decision.Tackle(ball)

/** Trait for players that can intercept the ball
  */
trait CanDecideToIntercept:
  self: Player =>

  /** Creates an intercept decision
    * @param ball
    *   the ball to intercept
    * @return
    *   an intercept decision
    */
  def createInterceptDecision(ball: Ball): Decision =
    Decision.Intercept(ball)

/** Trait for players that can move randomly
  */
trait CanDecideToMoveRandom:
  self: Player =>

  /** Creates a random movement decision
    * @param direction
    *   the direction to move
    * @param steps
    *   the number of steps to move
    * @return
    *   a random movement decision
    */
  def createRandomMovementDecision(direction: Direction, steps: Int): Decision =
    Decision.MoveRandom(direction, steps)

/** Trait for players that can receive passes
  */
trait CanDecideToReceivePass:
  self: Player =>

  /** Creates a receive pass decision
    * @param ball
    *   the ball to receive
    * @return
    *   a receive pass decision
    */
  def createReceivePassDecision(ball: Ball): Decision =
    Decision.ReceivePass(ball)

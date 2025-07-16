package dsl.decisions

import model.Match.*
import config.UIConfig
import model.Space.*
import config.MatchConfig
import dsl.`match`.TeamsSyntax.*
import config.Util

/** Common decision-making capabilities for all players.
  *
  * Provides basic movement and action decisions that are available to all player types.
  */
object CommonPlayerDecisions:

  extension (player: Player)

    /** Creates a run decision in the specified direction.
      *
      * @param direction
      *   The direction to run
      * @param steps
      *   The number of steps to run
      * @return
      *   A run decision
      */
    def createRunDecision(direction: Direction, steps: Int): Decision =
      Decision.Run(direction, steps)

    /** Generates all possible run directions for a player
      *
      * @param matchState
      *   The current match state
      * @return
      *   List of possible run decisions
      */
    def generatePossibleRunDirections(matchState: MatchState): List[Decision] =
      val validDirections =
        for
          dx <- -1 to 1
          dy <- -1 to 1
          if (dx == 0) != (dy == 0)
        yield player.createRunDecision(Direction(dx, dy), MatchConfig.runSteps)
      validDirections.toList

    /** Creates a confusion decision.
      *
      * @param remainingSteps
      *   The remaining confusion steps
      * @return
      *   A confusion decision
      */
    def createConfusionDecision(remainingSteps: Int): Decision =
      Decision.Confusion(remainingStep = remainingSteps)

    /** Creates a move to ball decision.
      *
      * @param directionToBall
      *   The direction towards the ball
      * @return
      *   A move to ball decision
      */
    def createMoveToBallDecision(directionToBall: Direction): Decision =
      Decision.MoveToBall(directionToBall)

/** Trait for players that can make passing decisions
  */
trait CanDecideToPass:
  self: Player =>

  /** Creates a pass decision to another player.
    *
    * @param target
    *   The target player
    * @return
    *   A pass decision
    */
  def createPassDecision(target: Player): Decision =
    Decision.Pass(this, target)

  /** Generates all possible pass decisions for available teammates.
    *
    * @param matchState
    *   The current match state
    * @return
    *   List of possible pass decisions
    */
  def generatePossiblePasses(matchState: MatchState): List[Decision] =
    for
      teammate <- matchState.teams.teamOf(this).players.filter(_.id != this.id)
    yield this.createPassDecision(teammate)

/** Trait for players that can make shooting decisions.
  */
trait CanDecideToShoot:
  self: Player =>

  /** Creates a shoot decision towards a goal position.
    *
    * @param goalPosition
    *   The target goal position
    * @return
    *   A shoot decision
    */
  def createShootDecision(goalPosition: Position): Decision =
    Decision.Shoot(this, goalPosition)

  /** Generates all possible shoot decisions towards goal positions.
    *
    * @param matchState
    *   The current match state
    * @return
    *   List of possible shoot decisions
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
    if Util.isPlayerInWestTeam(this, matchState) then UIConfig.goalEastX else UIConfig.goalWestX

/** Trait for players that can move towards the goal
  */
trait CanDecideToMoveToGoal:
  self: Player =>

  /** Creates a move to goal decision.
    *
    * @param direction
    *   The direction towards the goal
    * @return
    *   A move to goal decision
    */
  def createMoveToGoalDecision(direction: Direction): Decision =
    Decision.MoveToGoal(direction)

  /** Generates possible move to goal decisions.
    *
    * @param matchState
    *   The current match state
    * @return
    *   List of possible move to goal decisions
    */
  def generatePossibleMovesToGoal(matchState: MatchState): List[Decision] =
    val goalPosition    = determineGoalPosition(matchState)
    val directionToGoal = this.position.getDirection(goalPosition)
    List(createMoveToGoalDecision(directionToGoal))

  private def determineGoalPosition(matchState: MatchState): Position =
    if Util.isPlayerInWestTeam(this, matchState) then
      Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
    else
      Position(0, UIConfig.fieldHeight / 2)

/** Trait for players that can mark opponents.
  */
trait CanDecideToMark:
  self: Player =>

  /** Creates a mark decision.
    *
    * @param target
    *   The player to mark
    * @param teamSide
    *   The team identifier
    * @return
    *   A mark decision
    */
  def createMarkDecision(target: Player, teamSide: Side): Decision =
    Decision.Mark(this, target, teamSide)

/** Trait for players that can tackle the ball.
  */
trait CanDecideToTackle:
  self: Player =>

  /** Creates a tackle decision.
    *
    * @param ball
    *   The ball to tackle
    * @return
    *   A tackle decision
    */
  def createTackleDecision(ball: Ball): Decision =
    Decision.Tackle(ball)

/** Trait for players that can intercept the ball
  */
trait CanDecideToIntercept:
  self: Player =>

  /** Creates an intercept decision.
    *
    * @param ball
    *   The ball to intercept
    * @return
    *   An intercept decision
    */
  def createInterceptDecision(ball: Ball): Decision =
    Decision.Intercept(ball)

/** Trait for players that can move randomly
  */
trait CanDecideToMoveRandom:
  self: Player =>

  /** Creates a random movement decision.
    *
    * @param direction
    *   The direction to move
    * @param steps
    *   The number of steps to move
    * @return
    *   A random movement decision
    */
  def createRandomMovementDecision(direction: Direction, steps: Int): Decision =
    Decision.MoveRandom(direction, steps)

/** Trait for players that can receive passes
  */
trait CanDecideToReceivePass:
  self: Player =>

  /** Creates a receive pass decision.
    *
    * @param ball
    *   The ball to receive
    * @return
    *   A receive pass decision
    */
  def createReceivePassDecision(ball: Ball): Decision =
    Decision.ReceivePass(ball)

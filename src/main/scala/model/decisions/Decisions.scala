package model.decisions

import model.Match.*
import config.UIConfig
import model.decisions.PlayerDecisionFactory.*
import model.Space.*
import config.MatchConfig

object CommonPlayerDecisions:
  extension (player: Player)
    def decideRun(direction: Direction): Decision              = Decision.Run(direction)
    def decideConfusion(step: Int): Decision                   = Decision.Confusion(remainingStep = step)
    def decideMoveToBall(directionToBall: Direction): Decision = Decision.MoveToBall(directionToBall)
    def possibleMoveToBall(matchState: MatchState): List[Decision] =
      if player.decision != Decision.Initial
      then
        List(Decision.MoveToBall(player.position.getDirection(matchState.ball.position)))
      else List()
    def possibleMoves(matchState: MatchState): List[Decision] =
      val goalPosition: Position =
        if matchState.teams.head.players.contains(this)
        then Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
        else Position(0, UIConfig.fieldHeight / 2)
      val runDirections =
        for
          dx <- -1 to 0
          dy <- -1 to 1
          if dx != 0 || dy != 0
        yield player.decideRun(Direction(dx, dy))
      runDirections.toList

trait CanDecideToPass:
  self: Player =>
  def decidePass(to: Player): Decision = Decision.Pass(this, to)
  def possiblePasses(state: MatchState): List[Decision] =
    for
      team     <- state.teams.filter(_.players.contains(this))
      teammate <- team.players.filter(!_.equals(this))
    yield this.decidePass(teammate)

trait CanDecideToShoot:
  self: Player =>
  def decideShoot(goal: Position): Decision = Decision.Shoot(this, goal)
  def possibleShots(matchState: MatchState): List[Decision] =
    if this.decision != Decision.Initial
    then
      val goalX: Int =
        if matchState.teams.head.players.contains(this)
        then UIConfig.goalEastX
        else UIConfig.goalWestX
      val goalPositions: List[Position] = List(
        Position(goalX, UIConfig.firstPoleY),
        Position(goalX, UIConfig.midGoalY),
        Position(goalX, UIConfig.secondPoleY)
      )
      goalPositions.map(this.decideShoot)
    else List()

trait CanDecideToMoveToGoal:
  self: Player =>
  def decideMoveToGoal(direction: Direction): Decision = Decision.MoveToGoal(direction)
  def possibleMovesToGoal(matchState: MatchState): List[Decision] =
    if this.decision != Decision.Initial
    then
      val goalPosition: Position =
        if matchState.teams.head.players.contains(this)
        then Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
        else Position(0, UIConfig.fieldHeight / 2)
      List(Decision.MoveToGoal(this.position.getDirection(goalPosition)))
    else List()

trait CanDecideToMark:
  self: Player =>
  def decideMark(target: Player): Decision = Decision.Mark(this, target)
  def possibleMarks(matchState: MatchState): List[Decision] =
    List()

trait CanDecideToTackle:
  self: Player =>
  def decideTackle(ball: Ball): Decision = Decision.Tackle(ball)
  def possibleTackles(matchState: MatchState): List[Decision] =
    if matchState.ball.position.getDistance(this.position) <= MatchConfig.tackleRange
    then List(Decision.Tackle(matchState.ball))
    else List()

trait CanDecideToIntercept:
  self: Player =>
  def decideIntercept(ball: Ball): Decision = Decision.Intercept(ball)
  def possibleIntercepts(matchState: MatchState): List[Decision] =
    if matchState.ball.position.getDistance(this.position) <= MatchConfig.interceptBallRange
    then List(Decision.Intercept(matchState.ball))
    else List()

trait CanDecideToMoveRandom:
  self: Player =>
  def decideMoveRandom(direction: Direction, steps: Int): Decision =
    Decision.MoveRandom(direction, steps)
  def possibleMovesRandom(matchState: MatchState): List[Decision] =
    val directions = List(Direction(1, 0), Direction(0, 1), Direction(-1, 0), Direction(0, -1))
    directions.map(direction => this.decideMoveRandom(direction, MatchConfig.moveRandomSteps))

trait CanDecideToReceivePass:
  self: Player =>
  def decideReceivePass(ball: Ball): Decision = Decision.ReceivePass(ball)
  def possibleReceives(matchState: MatchState): List[Decision] =
    if matchState.ball.position.getDistance(this.position) <= MatchConfig.interceptBallRange
    then List(this.decideReceivePass(matchState.ball))
    else List()

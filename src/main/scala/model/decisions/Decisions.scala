package model.decisions

import model.Match.*
import config.UIConfig
import model.decisions.PlayerDecisionFactory.*
import model.Space.*

object CommonPlayerDecisions:
  extension (player: Player)
    def decideRun(direction: Direction): Decision              = Decision.Run(direction)
    def decideConfusion(step: Int): Decision                   = Decision.Confusion(remainingStep = step)
    def decideMoveToBall(directionToBall: Direction): Decision = Decision.MoveToBall(directionToBall)
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
    val goalX: Int =
      if matchState.teams.head.players.contains(this)
      then UIConfig.goalEastX
      else UIConfig.goalWestX
    val goalPositions: List[Position] = List(
      Position(goalX, UIConfig.firstPoleY),
      Position(goalX, UIConfig.midGoalY),
      Position(goalX, UIConfig.secondPoleY)
    )
    goalPositions.map(this.asControlDecisionPlayer.decideShoot)

trait CanDecideToMoveToGoal:
  self: Player =>
  def decideMoveToGoal(direction: Direction): Decision = Decision.MoveToGoal(direction)
  def possibleMovesToGoal(matchState: MatchState): List[Decision] =
    val goalPosition: Position =
      if matchState.teams.head.players.contains(this)
      then Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
      else Position(0, UIConfig.fieldHeight / 2)
    List(Decision.MoveToGoal(this.position.getDirection(goalPosition)))

trait CanDecideToMark:
  self: Player =>
  def decideMark(target: Player): Decision = Decision.Mark(this, target)

trait CanDecideToTackle:
  self: Player =>
  def decideTackle(ball: Ball): Decision = Decision.Tackle(ball)

trait CanDecideToIntercept:
  self: Player =>
  def decideIntercept(ball: Ball): Decision = Decision.Intercept(ball)

trait CanDecideToMoveRandom:
  self: Player =>
  def decideMoveRandom(direction: Direction, steps: Int): Decision =
    Decision.MoveRandom(direction, steps)

trait CanDecideToReceivePass:
  self: Player =>
  def decideReceivePass(ball: Ball): Decision = Decision.ReceivePass(ball)

package model

import scala.annotation.targetName

object Model:

  case class Position(x: Int, y: Int)

  opaque type Direction = (Double, Double)

  object Direction:
    def apply(p: Position): Direction    = (p.x, p.y)
    def apply(x: Int, y: Int): Direction = (x, y)
    def none: Direction                  = (0, 0)
  extension (d: Direction)
    def x: Double = d._1
    def y: Double = d._2

  case class Movement(direction: Direction, speed: Int)
  object Movement:
    def still: Movement = Movement(Direction.none, 0)

  def signum(i: Int): Int = if i < 0 then -1 else if i > 0 then 1 else 1
  extension (p: Position)
    def getDirection(to: Position): Direction =
      val dx = to.x - p.x
      val dy = to.y - p.y
      (signum(dx), signum(dy))

    @targetName("applyMovement")
    def +(m: Movement): Position =
      Position((p.x + m.direction.x * m.speed).toInt, (p.y + m.direction.y * m.speed).toInt)

  enum Action:
    case Move(direction: Direction, speed: Int)
    case Hit(direction: Direction, speed: Int)

  enum PlayerStatus:
    case ballControl
    case teamControl
    case noControl

  case class Player(
      id: Int,
      position: Position,
      status: PlayerStatus,
      nextAction: Option[Action] = None,
      movement: Movement
  )

  enum PlayerTeam:
    case TeamA
    case TeamB

  case class Team(id: Int, players: List[Player])

  case class Ball(position: Position, movement: Movement)

  case class MatchState(teams: List[Team], ball: Ball)

  enum Event:
    case StepEvent
    case DecideEvent
    case ActEvent
    case GoalEvent
    case RestartEvent

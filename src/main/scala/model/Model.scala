package model

import scala.annotation.targetName

object Model:

  case class Position(x: Int, y: Int)

  opaque type Direction = Position

  object Direction:
    def apply(p: Position): Direction    = p
    def apply(x: Int, y: Int): Direction = Position(x, y)
    def none: Direction                  = Position(0, 0)
    extension (d: Direction)
      def x: Int = d.x
      def y: Int = d.y

  case class Movement(direction: Direction, speed: Int)
  object Movement:
    def still: Movement = Movement(Direction.none, 0)

  extension (p: Position)
    def getDirection(to: Position): Direction =
      val dx = to.x - p.x
      val dy = to.y - p.y
      Direction(dx / (dx + dy), dy / (dx + dy))

    @targetName("applyMovement")
    def +(m: Movement): Position =
      Position(p.x + m.direction.x * m.speed, p.y + m.direction.y * m.speed)

  enum Action:
    case Move(direction: Direction)
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

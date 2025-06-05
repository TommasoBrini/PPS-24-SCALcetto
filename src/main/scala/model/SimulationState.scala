package model

object Model:

  case class Position(x: Int, y: Int)

  opaque type Direction = Position

  object Direction:
    def apply(x: Int, y: Int): Direction = Position(x, y)
    def zero: Direction                  = Position(0, 0)
    extension (d: Direction)
      def x: Int = d.x
      def y: Int = d.y

  case class Movement(direction: Direction, speed: Int)

  enum Action:
    case Move(target: Position)
    case Pass(target: Position, speed: Int)
    case Shoot(target: Position, speed: Int)

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

  case class Team(id: Int, players: List[Player])

  case class Ball(position: Position, movement: Movement)

  case class SimulationState(teams: List[Team], ball: Ball)

  enum Event:
    case Step
    case Decide
    case Act
    case Goal
    case Restart

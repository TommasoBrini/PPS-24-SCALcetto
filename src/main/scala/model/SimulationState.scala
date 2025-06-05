package model
object Model:

  type PlayerId = Int
  type TeamId   = Int
  type Speed    = Int

  case class Position(x: Int, y: Int)

  case class Movement(direction: Position, speed: Speed)

  enum Action:
    case Move(target: Position)
    case Pass(target: Position, speed: Int)
    case Shoot(target: Position, speed: Int)

  enum PlayerStatus:
    case ballControl
    case teamControl
    case noControl

  case class Player(
      id: PlayerId,
      position: Position,
      status: PlayerStatus,
      nextAction: Option[Action] = None,
      movement: Movement
  )

  case class Team(id: TeamId, players: List[Player])

  case class Ball(position: Position, movement: Movement)

  case class SimulationState(teams: List[Team], ball: Ball)

  enum Event:
    case Step
    case Decide
    case Act
    case Goal
    case Restart

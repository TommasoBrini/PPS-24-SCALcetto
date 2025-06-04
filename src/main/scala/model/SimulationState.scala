package model

case class Position(x: Int, y: Int)

case class Player(
    id: Int,
    position: Position,
    ballControl: Boolean,
    nextAction: Option[Action] = None,
    movement: Movement
)

case class Team(id: Int, players: List[Player])

case class Ball(position: Position, movement: Movement)

case class SimulationState(teams: List[Team], ball: Ball)

case class Movement(position: Position, speed: Int)

enum Event:
  case Step
  case Decide
  case Act
  case Goal
  case Restart

enum PlayerStatus:
  case ballControl
  case teamControl
  case noControl

enum Action:
  case Move(target: Position)
  case Pass(target: Position, speed: Int)
  case Shoot(target: Position, speed: Int)

package model

case class Position(x: Int, y: Int)

case class Player(id: Int, position: Position, status: Boolean)

case class Ball(position: Position)

case class SimulationState(playerList: List[Player], ball: Ball)

enum Event:
  case Step
  case Decision
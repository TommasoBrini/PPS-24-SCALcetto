package model
import Model.*

object Player:
  enum Action:
    case Move(target: Position)
    case Pass(target: Position, speed: Int)
    case Shoot(target: Position, speed: Int)
    case Initial

  enum PlayerStatus:
    case ballControl
    case teamControl
    case noControl

  case class Player(
      id: Int,
      position: Position,
      status: PlayerStatus,
      nextAction: Action = Action.Initial
  )

  trait PlayerBehavior:
    def decide(player: Player, state: SimulationState): Action

  object NoControlBehavior extends PlayerBehavior:
    def decide(player: Player, state: SimulationState): Action =
      Action.Move(state.ball.position)

  object TeamControlBehavior extends PlayerBehavior:
    def decide(player: Player, state: SimulationState): Action =
      val dx: Int = scala.util.Random.between(-1, 2)
      val dy: Int = scala.util.Random.between(-1, 2)
      Action.Move(Position(player.position.x + dx, player.position.y + dy))

  object BallControlBehavior extends PlayerBehavior:
    def decide(player: Player, state: SimulationState): Action = ???

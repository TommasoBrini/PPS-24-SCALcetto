package model
import Model.*
import config.FieldConfig.*

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
      nextAction: Action = Action.Initial,
      movement: Movement = Movement(Direction(0, 0), 0)
  )

  trait PlayerBehavior:
    def decide(player: Player, state: SimulationState): Action

  object NoControlBehavior extends PlayerBehavior:
    def decide(player: Player, state: SimulationState): Action =
      Action.Move(state.ball.position)

  object TeamControlBehavior extends PlayerBehavior:
    def decide(player: Player, state: SimulationState): Action =
      val (dx, dy) =
        if player.movement.speed == 0 then
          (scala.util.Random.between(-1, 2), scala.util.Random.between(-1, 2))
        else
          (player.movement.direction.x, player.movement.direction.y)

      val newX = (player.position.x + dx).max(3).min(fieldWidth * scale - 4)
      val newY = (player.position.y + dy).max(3).min(fieldHeight * scale - 4)
      Action.Move(Position(newX, newY))

  object BallControlBehavior extends PlayerBehavior:
    def decide(player: Player, state: SimulationState): Action = ???

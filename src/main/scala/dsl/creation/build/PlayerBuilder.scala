package dsl.creation.build

import model.Match.*

final class PlayerBuilder(id: Int):
  private var position: Position = Position(0, 0)
  private var motion: Movement   = Movement.still
  private var withBall: Boolean  = false
  private var decision: Decision = Decision.Initial
  private var action: Action     = Action.Initial

  def at(x: Int, y: Int): PlayerBuilder =
    position = Position(x, y)
    this

  def move(dir: Direction)(speed: Int): PlayerBuilder =
    motion = Movement(Direction(dir.x, dir.y), speed)
    this

  def ownsBall(hasBall: Boolean): PlayerBuilder =
    withBall = hasBall
    this

  def decidedTo(playerDecision: Decision): PlayerBuilder =
    decision = playerDecision
    this

  def isGoingTo(playerAction: Action): PlayerBuilder =
    action = playerAction
    this

  def build(): Player =
    val ball = if withBall then Some(Ball(position)) else None
    Player(id, position, motion, ball, decision, action)

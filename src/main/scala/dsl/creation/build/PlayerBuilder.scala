package dsl.creation.build

import model.Match.*

final class PlayerBuilder(id: Int):
  private var pos: Position        = Position(0, 0)
  private var mov: Movement        = Movement.still
  private var ballPresent: Boolean = false
  private var decision: Decision   = Decision.Initial
  private var action: Action       = Action.Initial

  /* DSL words */
  def at(x: Int, y: Int): PlayerBuilder = {
    pos = Position(x, y)
    this
  }

  def move(dir: Direction)(speed: Int): PlayerBuilder =
    mov = Movement(Direction(dir.x, dir.y), speed)
    this

  def ownsBall(hasBall: Boolean): PlayerBuilder = {
    ballPresent = true
    this
  }

  def decision(d: Decision): PlayerBuilder = {
    decision = d
    this
  }

  def nextAction(a: Action): PlayerBuilder = {
    action = a
    this
  }

  // materialise immutable Player
  def build(): Player =
    val ball = if ballPresent then Some(Ball(pos)) else None
    Player(id, pos, mov, ball, decision, action)

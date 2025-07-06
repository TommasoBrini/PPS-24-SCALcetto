package dsl.creation.build

import model.Space.{Direction, Movement, Position}
import model.Match.Ball

final class BallBuilder:
  private var pos: Position = Position(0, 0)
  private var mov: Movement = Movement.still

  def at(x: Int, y: Int): this.type =
    pos = Position(x, y)
    this
  def move(dir: Direction)(speed: Int): this.type =
    mov = Movement(Direction(dir.x, dir.y), speed)
    this

  def build(): Ball = Ball(pos, mov)

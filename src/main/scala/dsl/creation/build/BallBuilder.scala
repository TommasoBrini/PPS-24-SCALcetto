package dsl.creation.build

import model.Space.{Direction, Movement, Position}
import model.Match.Ball

final class BallBuilder:
  private var position: Position = Position(0, 0)
  private var motion: Movement   = Movement.still

  def at(x: Int, y: Int): BallBuilder =
    position = Position(x, y)
    this
  def move(dir: Direction, speed: Int): BallBuilder =
    motion = Movement(Direction(dir.x, dir.y), speed)
    this

  def build(): Ball = Ball(position, motion)

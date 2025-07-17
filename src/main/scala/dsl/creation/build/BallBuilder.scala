package dsl.creation.build

import model.Space.{Direction, Movement, Position}
import model.Match.Ball

/** DSL builder that creates an immutable [[Ball]] to be inserted in a newly generated `Match`. Mutability is local to
  * the builder; the resulting ball is fully immutable.
  */
final class BallBuilder:
  private var position: Position = Position(0, 0)
  private var motion: Movement   = Movement.still

  /** Sets the initial position of the ball. */
  def at(x: Int, y: Int): BallBuilder =
    position = Position(x, y)
    this

  /** Assigns an initial movement vector to the ball. */
  def move(dir: Direction, speed: Int): BallBuilder =
    motion = Movement(Direction(dir.x, dir.y), speed)
    this

  /** Produces the immutable ball with the configured state.
    *
    * @return
    *   the ball ready to be embedded in a match
    */
  def build(): Ball = Ball(position, motion)

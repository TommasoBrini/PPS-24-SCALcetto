package dsl.space

import dsl.space.DirectionSyntax.getDirectionFrom
import model.Space.{Bounce, Movement}

import scala.annotation.targetName

/** Amplifies a [[model.Space.Movement]] with helpers for **bounces** and scalar multiplication.
  */
object MovementSyntax:
  extension (m: Movement)
    /** Reflects the movement vector according to the bounce type.
      *
      * @return
      *   a new movement pointing away from the wall
      */
    def getMovementFrom(bounce: Bounce): Movement = Movement(m.direction getDirectionFrom bounce, m.speed)

    /** Scales the speed by the given factor.
      *
      * val slow = fast * 2 // half the speed
      */
    @targetName("applyScale")
    def *(factor: Int): Movement = Movement(m.direction, m.speed * factor)

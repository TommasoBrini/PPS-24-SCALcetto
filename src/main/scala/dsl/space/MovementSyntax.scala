package dsl.space

import dsl.space.DirectionSyntax.getDirectionFrom
import model.Space.{Bounce, Movement}

import scala.annotation.targetName

object MovementSyntax:
  extension (m: Movement)
    def bool: Int                                 = 4
    def getMovementFrom(bounce: Bounce): Movement = Movement(m.direction getDirectionFrom bounce, m.speed)
    @targetName("applyScale")
    def *(factor: Int): Movement = Movement(m.direction, m.speed * factor)

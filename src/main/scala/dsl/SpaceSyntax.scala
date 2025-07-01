package dsl

import config.UIConfig
import model.Space.*
import model.Space.Bounce.*

object SpaceSyntax {
  export DirectionSyntax.*
  export MovementSyntax.*
  export PositionSyntax.*

  object PositionSyntax:
    def checkAxisOutOfBound(coordinate: Int, axisBound: Int): Boolean =
      coordinate < 0 || coordinate > axisBound
    extension (p: Position)
      def isOutOfBound(widthBound: Int, heightBound: Int): Boolean =
        checkAxisOutOfBound(p.x, widthBound) || checkAxisOutOfBound(p.y, heightBound)

      def getBounce(widthBound: Int, heightBound: Int): Bounce =
        if checkAxisOutOfBound(p.x, widthBound) && checkAxisOutOfBound(p.y, heightBound) then ObliqueBounce
        else if checkAxisOutOfBound(p.x, widthBound) then HorizontalBounce
        else VerticalBounce

      def isGoal: Boolean =
        val firstGoalPost: Int  = (UIConfig.fieldHeight - UIConfig.goalHeight) / 2
        val secondGoalPost: Int = firstGoalPost + UIConfig.goalHeight
        (p.x <= 0 || p.x >= UIConfig.fieldWidth) && (p.y >= firstGoalPost && p.y <= secondGoalPost)

  object DirectionSyntax:
    private def bounceCalculator(bounce: Bounce, x: Double, y: Double): Direction = bounce match
      case ObliqueBounce    => Direction(-x, -y)
      case HorizontalBounce => Direction(-x, y)
      case VerticalBounce   => Direction(x, -y)
    extension (d: Direction)
      def getDirectionFrom(bounce: Bounce): Direction = bounceCalculator(bounce, d.x, d.y)

  object MovementSyntax:
    extension (m: Movement)
      def bool: Int                                 = 4
      def getMovementFrom(bounce: Bounce): Movement = m.copy(direction = m.direction.getDirectionFrom(bounce))

}

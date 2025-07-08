package dsl.game

import model.Match.{Ball, Player}
import model.Space.Direction

object BallSyntax:
  private def isHeadingToward(player: Player, tolerance: Double, ball: Ball): Boolean =
    val toPlayer: Direction = ball.position.getDirection(player.position)
    val actual: Direction   = ball.movement.direction
    Math.abs(actual.x - toPlayer.x) + Math.abs(actual.y - toPlayer.y) < tolerance
  extension (ball: Ball)
    def headingToward(player: Player, tolerance: Double): Boolean = isHeadingToward(player, tolerance, ball)

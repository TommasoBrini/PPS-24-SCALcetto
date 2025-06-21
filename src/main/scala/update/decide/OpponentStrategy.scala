package update.decide

import config.FieldConfig
import model.Match.{Action, Ball, Decision, MatchState, Player}

import scala.util.Random

object OpponentStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Decision =
    val ball: Ball = matchState.ball
    val dx: Int    = Math.abs(player.position.x - ball.position.x)
    val dy: Int    = Math.abs(player.position.y - ball.position.y)
    val nextDecision: Decision = player.decision match
      case Decision.Confusion(step) if step > 0 => Decision.Confusion(step - 1)
      case _ =>
        if dx < FieldConfig.takeBallRange && dy < FieldConfig.takeBallRange
        then Decision.Tackle(ball)
        else Decision.MoveToBall(player.position.getDirection(ball.position), FieldConfig.playerSpeed)
    nextDecision

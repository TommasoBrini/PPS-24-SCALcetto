package update.decide

import config.FieldConfig
import model.Match.{Action, Ball, MatchState, Player}

import scala.util.Random

object OpponentStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Action =
    val ball: Ball = matchState.ball
    val dx: Int    = Math.abs(player.position.x - ball.position.x)
    val dy: Int    = Math.abs(player.position.y - ball.position.y)
    val nextAct: Action = player.nextAction match
      case Action.Stopped(step) if step > 0 => player.nextAction
      case _ =>
        if dx < FieldConfig.takeBallRange && dy < FieldConfig.takeBallRange
        then Action.Take(ball)
        else Action.Move(player.position.getDirection(ball.position), FieldConfig.playerSpeed)
    nextAct

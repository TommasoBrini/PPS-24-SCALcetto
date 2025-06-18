package update.decide

import config.FieldConfig
import model.Match.{Action, Ball, MatchState, Player}

object OpponentStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player =
    val ball: Ball = matchState.ball
    val dx: Int    = Math.abs(player.position.x - ball.position.x)
    val dy: Int    = Math.abs(player.position.y - ball.position.y)
    val act: Action =
      if dx < FieldConfig.takeBallRange && dy < FieldConfig.takeBallRange
      then Action.Take(ball)
      else Action.Move(player.position.getDirection(ball.position), FieldConfig.playerSpeed)
    player.copy(
      nextAction = act
    )

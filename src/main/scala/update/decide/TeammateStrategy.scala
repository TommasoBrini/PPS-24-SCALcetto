package update.decide

import config.FieldConfig
import model.Match.Action.Stopped
import model.Match.Decision.{Confusion, MoveRandom, MoveToBall, ReceivePass}
import model.Match.{Action, Decision, MatchState, Player}
import model.Space.Direction

import scala.util.Random

object TeammateStrategy extends DecisionStrategy:
  def decide(player: Player, state: MatchState): Decision =
    if state.ball.isHeadingToward(player, FieldConfig.passDirectionRange)
    then
      if player.position.getDistance(state.ball.position) < FieldConfig.interceptBallRange
      then ReceivePass(state.ball)
      else MoveToBall(player.position.getDirection(state.ball.position))
    else
      player.nextAction match
        case Stopped(steps) if steps > 0 => Confusion(steps - 1)
        case _ =>
          player.decision match
            case MoveRandom(dir, steps) if steps > 0 => MoveRandom(dir, steps - 1)
            case _ =>
              val direction = Direction(Random.between(-1.toDouble, 1), Random.between(-1.toDouble, 1))
              MoveRandom(direction, FieldConfig.moveRandomSteps)

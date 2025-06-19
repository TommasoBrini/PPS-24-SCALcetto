package update.decide

import config.FieldConfig
import model.Match.Action.Stopped
import model.Match.{Action, MatchState, Player}
import model.Space.Direction

import scala.util.Random

object TeammateStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Action =
    val dx: Int   = Random.between(-1, 2)
    val dy: Int   = Random.between(-1, 2)
    val direction = Direction(dx, dy)
    val nextAction = player.nextAction match
      case Stopped(step) if step > 0 => player.nextAction
      case _                         => Action.Move(direction, FieldConfig.playerSpeed)
    nextAction

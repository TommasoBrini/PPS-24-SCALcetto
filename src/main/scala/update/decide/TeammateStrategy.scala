package update.decide

import config.FieldConfig
import model.Match.{Action, Decision, MatchState, Player}
import model.Space.Direction

import scala.util.Random

object TeammateStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Decision =
    val dx: Int   = Random.between(-1, 2)
    val dy: Int   = Random.between(-1, 2)
    val direction = Direction(dx, dy)
    val nextDecision: Decision = player.nextAction match
      case Action.Stopped(step) if step > 0 => Decision.Confusion(step - 1)
      case _                                => Decision.MoveRandom(direction)
    nextDecision

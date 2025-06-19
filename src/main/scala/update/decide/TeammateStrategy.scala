package update.decide

import config.FieldConfig
import model.Match.{Decision, MatchState, Player}
import model.Space.Direction

import scala.util.Random

object TeammateStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Decision =
    val dx: Int   = Random.between(-1, 2)
    val dy: Int   = Random.between(-1, 2)
    val direction = Direction(dx, dy)
    val nextDecision: Decision = player.decision match
      // TODO understand how to implement effectively this Stop
      case Decision.Confusion(step) if step > 0 => Decision.Initial
      case _                                    => Decision.MoveToBall(direction, FieldConfig.playerSpeed)
    nextDecision

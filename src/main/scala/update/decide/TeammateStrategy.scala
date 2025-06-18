package update.decide

import config.FieldConfig
import model.Match.{Action, MatchState, Player}
import model.Space.Direction

import scala.util.Random

object TeammateStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player =
    val dx: Int   = Random.between(-1, 2)
    val dy: Int   = Random.between(-1, 2)
    val direction = Direction(dx, dy)
    player.copy(
      nextAction = Some(Action.Move(direction, FieldConfig.playerSpeed))
    )

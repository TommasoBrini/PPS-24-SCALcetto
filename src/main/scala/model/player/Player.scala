package model.player

import config.UIConfig
import model.Match.*
import model.player
import model.Space.*
import model.decisions.*

case class Player(
    id: Int,
    position: Position,
    movement: Movement = Movement.still,
    ball: Option[Ball] = None,
    nextAction: Action = Action.Initial,
    decision: Decision = Decision.Initial
):
  def hasBall: Boolean = ball.isDefined

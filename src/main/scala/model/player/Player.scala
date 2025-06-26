package model.player

import model.Match.*
import model.player
import model.Space.*
import model.decisions.*
import model.player.Player.ControlPlayer

case class Player(
    id: Int,
    position: Position,
    movement: Movement = Movement.still,
    ball: Option[Ball] = None,
    nextAction: Action = Action.Initial,
    decision: Decision = Decision.Initial
) extends CanDecideToRun with CanDecideConfusion with CanDecideToMoveToBall:
  def hasBall: Boolean = ball.isDefined

  import model.player.Player.*
  def asControlPlayer: ControlPlayer = new Player(id, position, movement, ball, nextAction, decision)
    with CanDecideToPass with CanDecideToShoot with CanDecideToMoveToGoal

  def asOpponent: OpponentPlayer = new Player(id, position, movement, ball, nextAction, decision) with CanDecideToTackle
    with CanDecideToMark with CanDecideToIntercept

  def asTeammate: TeammatePlayer = new Player(id, position, movement, ball, nextAction, decision)
    with CanDecideToMoveRandom with CanDecideToReceivePass

object Player:
  type ControlPlayer  = Player & CanDecideToPass & CanDecideToShoot & CanDecideToMoveToGoal
  type OpponentPlayer = Player & CanDecideToMark & CanDecideToTackle & CanDecideToIntercept
  type TeammatePlayer = Player & CanDecideToMoveRandom & CanDecideToReceivePass

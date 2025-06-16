package update.act

import config.FieldConfig
import model.Match.*
import model.Match.Action.*
import model.Match.Event.*

object Act:
  def isAGoal(state: MatchState): Boolean = false

  def isBallOut(state: MatchState): Boolean =
    state.ball.position.isOutOfBound(FieldConfig.widthBound, FieldConfig.heightBound)

  def executeAction(state: MatchState): MatchState =
    move(updateMovements(state))

  private[update] def updateMovements(state: MatchState): MatchState =
    MatchState(
      state.teams.map(updateMovement),
      updateMovement(state.ball, state.teams.flatMap(_.players).find(_.hasBall))
    )

  private[update] def updateMovement(team: Team): Team =
    team.copy(players = team.players.map(updateMovement))

  private[update] def updateMovement(player: Player): Player =
    player.nextAction match
      case Some(Move(direction, FieldConfig.playerSpeed)) =>
        player.copy(movement = Movement(direction, FieldConfig.playerSpeed))
      case Some(Hit(_, _))  => player.copy(movement = Movement.still, ball = None)
      case Some(Take(ball)) => player.copy(movement = Movement.still, ball = Some(ball))
      case _                => player

  private[update] def updateMovement(ball: Ball, playerInControl: Option[Player]): Ball =
    val movement = playerInControl match
      case Some(Player(_, _, _, _, Some(Hit(direction, speed)))) => Movement(direction, speed)
      case Some(Player(_, _, movement, _, Some(Move(_, _))))     => movement
      case _                                                     => ball.movement
    ball.copy(movement = movement)

  private[update] def move(state: MatchState): MatchState =
    MatchState(state.teams.map(move), move(state.ball))

  private[update] def move(team: Team): Team =
    team.copy(players = team.players.map(move))

  private[update] def move(p: Player) =
    p.copy(position = p.position + p.movement)

  private[update] def move(ball: Ball): Ball =
    ball.copy(position = ball.position + ball.movement)

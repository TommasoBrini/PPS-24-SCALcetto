package update.act

import config.FieldConfig
import model.Model
import model.Model.*
import model.Model.Action.*
import model.Model.PlayerStatus.ballControl

object Act:
  def isAGoal(state: MatchState): Boolean = false

  def executeAction(state: MatchState): MatchState =
    move(updateMovements(state))

  private[update] def updateMovements(state: MatchState): MatchState =
    MatchState(
      state.teams.map(updateMovement),
      updateMovement(state.ball, state.teams.flatMap(_.players).find(_.status == ballControl))
    )

  private[update] def updateMovement(team: Team): Team =
    team.copy(players = team.players.map(updateMovement))

  private[update] def updateMovement(player: Player): Player =
    val movement = player.nextAction match
      case Some(Move(direction)) => Movement(direction, FieldConfig.playerSpeed)
      case Some(Hit(_, _))       => Movement.still
      case _                     => player.movement
    player.copy(movement = movement)

  private[update] def updateMovement(ball: Ball, playerInControl: Option[Player]): Ball =
    val movement = playerInControl match
      case Some(Player(_, _, _, Some(Hit(direction, speed)), _)) => Movement(direction, speed)
      case Some(player @ Player(_, _, _, Some(Move(_)), _))      => player.movement
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

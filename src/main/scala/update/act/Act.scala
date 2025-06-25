package update.act

import config.FieldConfig
import model.Match.{Action, *}
import model.Match.Action.*

object Act:
  def executeAction(state: MatchState): MatchState =
    move(updateMovements(state))

  private[update] def updateMovements(state: MatchState): MatchState =
    val newOwnerOpt = state.teams.flatMap(_.players).find(_.nextAction match
      case Take(_) => true
      case _       => false
    )
    val ballOwner: Option[Player] =
      if newOwnerOpt.isDefined then newOwnerOpt else state.teams.flatMap(_.players).find(_.hasBall)
    MatchState(
      state.teams.map(updateMovement(_, ballOwner)),
      updateMovement(state.ball, ballOwner)
    )

  private[update] def updateMovement(team: Team, ballOwner: Option[Player]): Team =
    team.copy(players = team.players.map(updateMovement(_, ballOwner)))

  private[update] def updateMovement(player: Player, ballOwner: Option[Player]): Player =
    if player.hasBall && ballOwner.isDefined && player.id != ballOwner.get.id then
      player.copy(movement = Movement.still, ball = None, nextAction = Action.Stopped(FieldConfig.stoppedAfterTackle))
    else
      player.nextAction match
        case Take(ball) =>
          player.copy(movement = Movement.still, ball = Some(ball))
        case Hit(_, _) =>
          player.copy(movement = Movement.still, ball = None, nextAction = Action.Stopped(FieldConfig.stoppedAfterHit))
        case Move(direction, FieldConfig.playerSpeed) =>
          player.copy(movement = Movement(direction, FieldConfig.playerSpeed))
        case Stopped(step) => player.copy(movement = Movement.still)
        case _             => player

  private[update] def updateMovement(ball: Ball, playerInControl: Option[Player]): Ball =
    val movement: Movement = playerInControl match
      case Some(Player(_, _, _, _, Hit(direction, speed), _)) => Movement(direction, speed)
      case Some(Player(_, _, movement, _, Move(_, _), _))     => movement
      case _                                                  => ball.movement
    val newPosition = playerInControl match
      case Some(Player(_, p, m, _, _, _)) => p + (m * (FieldConfig.ballSize / 2))
      case _                              => ball.position
    ball.copy(position = newPosition, movement = movement)

  private[update] def move(state: MatchState): MatchState =
    MatchState(state.teams.map(move), move(state.ball))

  private[update] def move(team: Team): Team =
    team.copy(players = team.players.map(move))

  private[update] def move(p: Player) =
    p.copy(position = p.position + p.movement)

  private[update] def move(ball: Ball): Ball =
    ball.copy(position = ball.position + ball.movement)

  def isAGoal(state: MatchState): Boolean =
    state.ball.position.isGoal

  def isBallOut(state: MatchState): Boolean =
    state.ball.position.isOutOfBound(FieldConfig.widthBound, FieldConfig.heightBound)

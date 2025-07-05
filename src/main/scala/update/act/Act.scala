package update.act

import config.UIConfig
import config.MatchConfig
import model.Match.{Action, Ball, MatchState, Movement, Team}
import model.Match.Action.*
import model.Match.Player
import model.decisions.PlayerRoleFactory.*
import dsl.SpaceSyntax.*

object Act:
  def act(state: MatchState): MatchState =
    val updateState: MatchState = move(updateMovements(state))
    updatePlayers(updateState)

  private[update] def updateMovements(state: MatchState): MatchState =
    val newOwnerOpt = state.teams.players.find(_.nextAction match
      case Take(_) => true
      case _       => false
    )
    val teamA = state.teams.teamA.copy(hasBall =
      if newOwnerOpt.isDefined then state.teams.teamA.players.contains(newOwnerOpt.get) else state.teams.teamA.hasBall
    )
    val teamB = state.teams.teamB.copy(hasBall =
      if newOwnerOpt.isDefined then state.teams.teamB.players.contains(newOwnerOpt.get) else state.teams.teamB.hasBall
    )

    val ballOwner: Option[Player] =
      if newOwnerOpt.isDefined then newOwnerOpt else state.teams.players.find(_.hasBall)

    val updateTeams: (Team, Team) = (updateMovement(teamA, ballOwner), updateMovement(teamB, ballOwner))

    MatchState(
      updateTeams,
      updateMovement(state.ball, ballOwner)
    )

  private[update] def updateMovement(team: Team, ballOwner: Option[Player]): Team =
    team.copy(players = team.players.map(updateMovement(_, ballOwner)))

  private[update] def updateMovement(player: Player, ballOwner: Option[Player]): Player =
    if player.hasBall && ballOwner.isDefined && player.id != ballOwner.get.id then
      player.copy(movement = Movement.still, ball = None, nextAction = Action.Stopped(MatchConfig.stoppedAfterTackle))
    else
      player.nextAction match
        case Take(ball) => player.copy(movement = Movement.still, ball = Some(ball))
        case Hit(_, _) =>
          player.copy(movement = Movement.still, ball = None, nextAction = Action.Stopped(MatchConfig.stoppedAfterHit))
        case Move(direction, speed) => player.copy(movement = Movement(direction, speed))
        case Stopped(step)          => player.copy(movement = Movement.still)
        case _                      => player

  private[update] def updateMovement(ball: Ball, playerInControl: Option[Player]): Ball =
    val movement: Movement = playerInControl match
      case Some(Player(_, _, _, _, Hit(direction, speed), _)) => Movement(direction, speed)
      case Some(Player(_, _, movement, _, Move(_, _), _))     => movement
      case _                                                  => ball.movement
    val newPosition = playerInControl match
      case Some(Player(_, p, m, _, _, _)) => p + (m * (UIConfig.ballSize / 2))
      case _                              => ball.position
    ball.copy(position = newPosition, movement = movement)

  private[update] def move(state: MatchState): MatchState =
    MatchState((move(state.teams.teamA), move(state.teams.teamB)), move(state.ball))

  private[update] def move(team: Team): Team =
    team.copy(players = team.players.map(move))

  private[update] def move(p: Player) =
    p.copy(position = (p.position + p.movement).clampToField)

  private[update] def move(ball: Ball): Ball =
    ball.copy(position = ball.position + ball.movement)

  def isAGoal(state: MatchState): Boolean =
    state.ball.position.isGoal

  def isBallOut(state: MatchState): Boolean =
    state.ball.position.isOutOfBound(UIConfig.fieldWidth, UIConfig.fieldHeight)

  private def updatePlayers(state: MatchState): MatchState =
    def updatedTeam(team: Team): Team =
      val newPlayers: List[Player] = team.players.map { player =>
        if !team.hasBall then
          player.asDefendingPlayer
        else if player.hasBall then
          player.asAttackingPlayer
        else {
          player.asTeammatePlayer
        }
      }
      team.copy(players = newPlayers)
    val newTeams: (Team, Team) = (updatedTeam(state.teams.teamA), updatedTeam(state.teams.teamB))
    state.copy(teams = newTeams)

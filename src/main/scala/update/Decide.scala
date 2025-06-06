package update

import model.Model.*

import scala.util.Random

object Decide:

  def takeDecisions(state: SimulationState): SimulationState =
    // players with ball -> decidePlayerControl()      -- MUNI
    // player in team with ball -> moveRandom()         --  TOM
    // players in team without ball -> decidePlayerMovement()  -- TOM
    state.copy(
      teams = state.teams.map(updateTeam)
    )

  private def updateTeam(team: Team): Team =
    team.copy(players = team.players.map(updatePlayer))

  private def updatePlayer(player: Player): Player = {
    player.status match {
      case PlayerStatus.teamControl => decideOfPlayerInTeamWithBall(player)
      case _                        => player
    }
  }

  private[update] def decideOfPlayerInTeamWithBall(player: Player): Player = {
    val dx          = Random.between(-1, 2)
    val dy          = Random.between(-1, 2)
    val newPosition = Position(player.position.x + dx, player.position.y + dy)
    player.copy(
      nextAction = Some(Action.Move(newPosition))
    )
  }

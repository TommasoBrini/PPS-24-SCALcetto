package update

import model.Model.*
import model.Player.*

import scala.util.Random

object Decide:

  def takeDecisions(state: SimulationState): SimulationState =
    // players with ball -> decidePlayerControl()      -- MUNI
    // player in team with ball -> moveRandom()         --  TOM
    // players in team without ball -> decidePlayerMovement()  -- TOM
    val updateTeams =
      for team <- state.teams yield
        val updatePlayers = for player <- team.players yield decideActionForPlayer(player, state)
        team.copy(players = updatePlayers)
    state.copy(teams = updateTeams)

  private[update] def decideActionForPlayer(player: Player, state: SimulationState): Player =
    val behavior = player.status match
      case PlayerStatus.ballControl => BallControlBehavior
      case PlayerStatus.teamControl => TeamControlBehavior
      case PlayerStatus.noControl   => NoControlBehavior
    player.copy(nextAction = behavior.decide(player, state))

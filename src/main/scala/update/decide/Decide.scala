package update.decide

import model.Model.*

import scala.util.Random

object Decide:

  def takeDecisions(state: MatchState): MatchState =
    // players with ball -> decidePlayerControl()      -- MUNI
    // player in team with ball -> moveRandom()         --  TOM
    // players in team without ball -> decidePlayerMovement()  -- TOM
    state.copy(
      teams = state.teams.map { team =>
        team.copy(players = team.players.map { player =>
          player.status match
            case PlayerStatus.teamControl => decideOfPlayerInTeamWithBall(player)
            case PlayerStatus.noControl   => decideOfPlayerWithNoControl(player, state.ball.position)
            case _                        => player
        })
      }
    )

  private[update] def decideOfPlayerInTeamWithBall(player: Player): Player = {
    val dx: Int   = Random.between(-1, 2)
    val dy: Int   = Random.between(-1, 2)
    val direction = Direction(dx, dy)
    player.copy(
      nextAction = Some(Action.Move(direction))
    )
  }

  private[update] def decideOfPlayerWithNoControl(player: Player, ballPosition: Position): Player =
    player.copy(
      nextAction = Some(Action.Move(player.position.getDirection(ballPosition)))
    )

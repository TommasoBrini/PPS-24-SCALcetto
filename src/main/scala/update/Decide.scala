package update

import model.Model.*

import scala.util.Random

object Decide:

  def takeDecisions(state: SimulationState): SimulationState =
    // players with ball -> decidePlayerControl()      -- MUNI
    // player in team with ball -> moveRandom()         --  TOM
    // players in team without ball -> decidePlayerMovement()  -- TOM
    state.copy(
      teams = state.teams.map { team =>
        team.copy(players = team.players.map { player =>
          player.status match
            case PlayerStatus.teamControl => decideOfPlayerInTeamWithBall(player)
            case PlayerStatus.noControl   => decideOfPlayerWithNoControl(player, state.ball.position)
            case PlayerStatus.ballControl => decidePlayerControl(state.teams, player)
        })
      }
    )

  // TODO
  // 1. Spatial Awareness:
  //   => Evaluate positions of teammates, opponents, and goals using geometric calculations
  //   => Calculate safe zones using Voronoi diagrams or pitch control models (next)
  // 2. Action Selection:
  //   => compute success probability
  // 3. Passing
  //   => Use raycasting to check passing lanes
  //   => evaluate receiver's space and probability to complete the pass
  // 4. Dribbling
  //   => calculate potential advancement
  //   => if dribbles respawn behind the player
  // 5. Forward Moving
  private[update] def decidePlayerControl(teams: List[Team], player: Player): Player = (player, teams) match
    case (p, t) => ???
    case (p, t) => ???

  private[update] def passBall(matchState: SimulationState, ballPlayer: Player): Player = ???

  private[update] def moveForward(ballPlayer: Player): Player = ???

  private[update] def passSuccessRate(ballPlayer: Player): Double =
    Random.between(0, 1)

  private[update] def moveForwardSuccessRate(ballPlayer: Player): Double =
    Random.between(0, 1)

  private[update] def shootSuccessRate(ballPlayer: Player): Double =
    Random.between(0, 1)

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

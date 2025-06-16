package update.decide

import config.FieldConfig
import model.Match.*

import scala.util.Random
import config.FieldConfig.*

object Decide:

  def takeDecisions(state: MatchState): MatchState =
    // players with ball -> decidePlayerControl()      -- MUNI
    // player in team with ball -> moveRandom()         --  TOM
    // players in team without ball -> decidePlayerMovement()  -- TOM
    state.copy(
      teams = state.teams.map { team =>
        val teamPossession = team.players.exists(_.ball.isDefined)
        team.copy(players = team.players.map { player =>
          player.ball match
            case Some(_)             => decidePlayerControl(player, state.teams)
            case _ if teamPossession => decideOfPlayerInTeamWithBall(player)
            case _                   => player.copy(nextAction = decideOfPlayerWithNoControl(player, state.ball))
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
  private[update] def decidePlayerControl(ballPlayer: Player, teams: List[Team]): Player = (ballPlayer, teams) match
    case _ => passBall(ballPlayer, getClosestTeammate(ballPlayer, teams))

  private[update] def passBall(ballPlayer: Player, receivePlayer: Player): Player =
    ballPlayer.copy(
      nextAction = Some(Action.Hit(ballPlayer.position.getDirection(receivePlayer.position), FieldConfig.ballSpeed))
    )

  private def getClosestTeammate(ballPlayer: Player, teams: List[Team]): Player =
    teams.filter(_.players.contains(ballPlayer)).head.players.filter(_.id != ballPlayer.id).head

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
      nextAction = Some(Action.Move(direction, playerSpeed))
    )
  }

  private[update] def decideOfPlayerWithNoControl(player: Player, ball: Ball): Option[Action] =
    val dx = Math.abs(player.position.x - ball.position.x)
    val dy = Math.abs(player.position.y - ball.position.y)
    if dx < FieldConfig.takeBallRange && dy < FieldConfig.takeBallRange
    then Some(Action.Take(ball))
    else Some(Action.Move(player.position.getDirection(ball.position), playerSpeed))

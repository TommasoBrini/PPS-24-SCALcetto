package update.decide

import config.FieldConfig
import model.Model.{Action, Ball, Direction, MatchState, Player, Team}

import scala.util.Random

trait DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player

object BallPlayerStrategy extends DecisionStrategy:
  private def getClosestTeammate(ballPlayer: Player, teams: List[Team]): Player =
    teams.filter(_.players.contains(ballPlayer)).head.players.filter(_.id != ballPlayer.id).head

  def decide(player: Player, matchState: MatchState): Player =
    val receiver: Player = getClosestTeammate(player, matchState.teams)
    player.copy(
      nextAction = Some(Action.Hit(player.position.getDirection(receiver.position), FieldConfig.ballSpeed))
    )

object TeamPossesionStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player =
    val dx: Int   = Random.between(-1, 2)
    val dy: Int   = Random.between(-1, 2)
    val direction = Direction(dx, dy)
    player.copy(
      nextAction = Some(Action.Move(direction, FieldConfig.playerSpeed))
    )

object NoControlStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player =
    val ball: Ball = matchState.ball
    val dx: Int    = Math.abs(player.position.x - ball.position.x)
    val dy: Int    = Math.abs(player.position.y - ball.position.y)
    val act: Some[Action] =
      if dx < FieldConfig.takeBallRange && dy < FieldConfig.takeBallRange
      then Some(Action.Take(ball))
      else Some(Action.Move(player.position.getDirection(ball.position), FieldConfig.playerSpeed))
    player.copy(
      nextAction = act
    )

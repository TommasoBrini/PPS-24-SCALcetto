package update.decide

import config.FieldConfig
import model.Match.*

import scala.swing.PasswordField
import scala.util.Random

trait DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player

object BallPlayerStrategy extends DecisionStrategy:
  // TODO this should not be here
  enum BallPlayerAction:
    case Pass
    case Move
    case Shoot

  private def passSuccessRate(player: Player, matchState: MatchState): Double =
    1.0
  private def shootSuccessRate(player: Player, matchState: MatchState): Double =
    Random.between(0.0, 1.0)
  private def moveSuccessRate(player: Player, matchState: MatchState): Double =
    Random.between(0.0, 1.0)
  private def getClosestTeammate(ballPlayer: Player, teams: List[Team]): Player =
    teams.filter(_.players.contains(ballPlayer)).head.players.filter(_.id != ballPlayer.id).head

  def decide(player: Player, matchState: MatchState): Player =
    val receiver: Player = getClosestTeammate(player, matchState.teams)
    val successRateMap: Map[BallPlayerAction, (Player, MatchState) => Double] =
      Map(
        BallPlayerAction.Pass  -> passSuccessRate,
        BallPlayerAction.Move  -> moveSuccessRate,
        BallPlayerAction.Shoot -> shootSuccessRate
      )
    val bestAction: BallPlayerAction = successRateMap.maxBy(_._2(player, matchState))._1
    val action: Some[Action] = bestAction match
      case bestAction if bestAction == BallPlayerAction.Pass =>
        Some(Action.Hit(player.position.getDirection(receiver.position), FieldConfig.ballSpeed))
      case bestAction if bestAction == BallPlayerAction.Shoot => Some(null) // garbage
      case _                                                  => Some(null) // garbage
    player.copy(
      nextAction = action
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

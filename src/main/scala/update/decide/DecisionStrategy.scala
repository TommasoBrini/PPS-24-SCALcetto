package update.decide

import config.FieldConfig
import model.Match.*

import scala.util.Random

trait DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Player

object BallPlayerStrategy extends DecisionStrategy:

  private def passSuccessRate(action: Action, player: Player, matchState: MatchState): Double =
    Random.between(0.5, 0.9)

  private def shootSuccessRate(action: Action, player: Player, matchState: MatchState): Double =
    Random.between(0.0, 1.0)

  private def moveSuccessRate(action: Action, player: Player, matchState: MatchState): Double =
    Random.between(0.0, 1.0)

  private def getClosestTeammate(ballPlayer: Player, teams: List[Team]): Player =
    teams.filter(_.players.contains(ballPlayer)).head.players.filter(_.id != ballPlayer.id).head

  private def allPasses(player: Player, matchState: MatchState): List[Action] = ???

  private def allMove(player: Player, matchState: MatchState): List[Action] = ???

  private def allShoot(player: Player, matchState: MatchState): List[Action] = ???

  private def calculateBestAction(player: Player, state: MatchState): Action =

    val successActions: Map[Action, (Action, Player, MatchState) => Double] =
      allPasses(player, state).map((_, passSuccessRate)).toMap ++
        allMove(player, state).map((_, moveSuccessRate)) ++
        allShoot(player, state).map((_, shootSuccessRate))

    successActions.maxBy(t => t._2(t._1, player, state))._1

  def decide(player: Player, state: MatchState): Player =
    player.copy(
      nextAction = Some(calculateBestAction(player, state))
    )

object TeamPossessionStrategy extends DecisionStrategy:
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

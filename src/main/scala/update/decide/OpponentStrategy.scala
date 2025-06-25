package update.decide

import config.FieldConfig
import model.Match.Decision.MoveToBall
import model.Match.{Action, Ball, Decision, MatchState, Player}
import model.Space.Position

import scala.util.Random

object OpponentStrategy extends DecisionStrategy:
  def decide(player: Player, matchState: MatchState): Decision =

    val ballPlayerPosition: Option[Position] = matchState.teams.flatMap(_.players).find(_.hasBall) match
      case Some(ballPlayer) => Some(ballPlayer.position)
      case _                => None

    val ball: Ball  = matchState.ball
    val dxBall: Int = Math.abs(player.position.x - ball.position.x)
    val dyBall: Int = Math.abs(player.position.y - ball.position.y)
    val dxPlayer: Option[Int] = ballPlayerPosition match
      case Some(pos) => Some(Math.abs(player.position.x - pos.x))
      case None      => None
    val dyPlayer: Option[Int] = ballPlayerPosition match
      case Some(pos) => Some(Math.abs(player.position.y - pos.y))
      case None      => None

    val nextDecision: Decision = player.nextAction match
      case Action.Stopped(step) if step > 0 => Decision.Confusion(step - 1)
      case _ =>
        if dxPlayer.isDefined && dyPlayer.isDefined && dxPlayer.get < FieldConfig.tackleRange && dyPlayer.get < FieldConfig.tackleRange
        then
          Decision.Tackle(ball)
        else if dxPlayer.isEmpty && dxBall < FieldConfig.interceptBallRange && dyBall < FieldConfig.interceptBallRange
        then
          Decision.Intercept(ball)
        else
          Decision.MoveToBall(
            player.position.getDirection(ball.position),
            FieldConfig.playerSpeed
          )
    nextDecision

  private def mark(player: Player, state: MatchState): Decision =

    val opponentPlayers = state.teams
      .find(!_.players.contains(player))
      .map(_.players)
      .getOrElse(List.empty)

    println(s"Opponent players: ${opponentPlayers.map(_.id)}")

    val markedOpponentIds: Set[Int] = state.teams
      .flatMap(_.players)
      .flatMap(_.decision match
        case Decision.Mark(_, target) => Some(target.id)
        case _                        => None
      ).toSet

    val unmarkedOpponents = opponentPlayers.filterNot(p => markedOpponentIds.contains(p.id))

    if unmarkedOpponents.nonEmpty then
      val target = unmarkedOpponents.minBy(_.position.getDistance(player.position))
      Decision.Mark(player, target)
    else
      Decision.Confusion(2)

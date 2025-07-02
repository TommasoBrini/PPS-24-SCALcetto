package model.decisions.behavior
import config.MatchConfig
import model.Match.*
import model.decisions.DecisorPlayer.OpponentPlayer

object OpponentBehavior:
  extension (player: OpponentPlayer)
    def calculateBestDecision(matchState: MatchState, target: Option[Player]): Decision =
      val ballPlayerPosition: Option[Position] = matchState.teams.flatMap(_.players).find(_.hasBall) match
        case Some(ballPlayer) => Some(ballPlayer.position)
        case _                => None

      val ball: Ball            = matchState.ball
      val dxBall: Int           = Math.abs(player.position.x - ball.position.x)
      val dyBall: Int           = Math.abs(player.position.y - ball.position.y)
      val dxPlayer: Option[Int] = ballPlayerPosition.map(pos => Math.abs(player.position.x - pos.x))
      val dyPlayer: Option[Int] = ballPlayerPosition.map(pos => Math.abs(player.position.y - pos.y))

      val nextDecision: Decision = player.nextAction match
        case Action.Stopped(step) if step > 0 => Decision.Confusion(step - 1)
        case _ =>
          if dxPlayer.isDefined && dyPlayer.isDefined && dxPlayer.get < MatchConfig.tackleRange && dyPlayer.get < MatchConfig.tackleRange
          then
            Decision.Tackle(ball)
          else if dxPlayer.isEmpty && dxBall < MatchConfig.interceptBallRange && dyBall < MatchConfig.interceptBallRange
          then
            Decision.Intercept(ball)
          else
            target.map(t => Decision.Mark(player, t))
              .getOrElse(Decision.MoveToBall(player.position.getDirection(ball.position)))
      nextDecision

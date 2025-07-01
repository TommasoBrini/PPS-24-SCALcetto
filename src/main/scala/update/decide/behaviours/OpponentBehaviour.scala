package update.decide.behaviours
import config.MatchConfig
import model.Match.*

class OpponentBehaviour(target: Option[Player]) extends PlayerBehavior:

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

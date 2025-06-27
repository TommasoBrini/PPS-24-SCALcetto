package update.decide.behaviours
import config.FieldConfig
import model.Match.*
import model.player.Player

class OpponentBehavior(target: Option[Player]) extends PlayerBehavior:

  def decide(player: Player, matchState: MatchState): Decision = player match
    case opponent: Player.OpponentPlayer =>
      val ballPlayerPosition: Option[Position] = matchState.teams.flatMap(_.players).find(_.hasBall) match
        case Some(ballPlayer) => Some(ballPlayer.position)
        case _                => None
      val ball: Ball  = matchState.ball
      val dxBall: Int = Math.abs(opponent.position.x - ball.position.x)
      val dyBall: Int = Math.abs(opponent.position.y - ball.position.y)
      val dxPlayer: Option[Int] = ballPlayerPosition match
        case Some(pos) => Some(Math.abs(opponent.position.x - pos.x))
        case None      => None
      val dyPlayer: Option[Int] = ballPlayerPosition match
        case Some(pos) => Some(Math.abs(opponent.position.y - pos.y))
        case None      => None

      val nextDecision: Decision = opponent.nextAction match
        case Action.Stopped(step) if step > 0 => opponent.decideConfusion(step - 1)
        case _ =>
          if dxPlayer.isDefined && dyPlayer.isDefined && dxPlayer.get < FieldConfig.tackleRange && dyPlayer.get < FieldConfig.tackleRange
          then
            opponent.decideTackle(ball)
          else if dxPlayer.isEmpty && dxBall < FieldConfig.interceptBallRange && dyBall < FieldConfig.interceptBallRange
          then
            opponent.decideIntercept(ball)
          else
            target.map(t => opponent.decideMark(t))
              .getOrElse(opponent.decideMoveToBall(opponent.position.getDirection(ball.position)))
      nextDecision
    case _ =>
      throw new IllegalArgumentException("OpponentBehavior can only be used with OpponentPlayer instances")

package model.decisions.behavior
import config.MatchConfig
import model.Match.*
import model.decisions.DecisorPlayer.OpponentPlayer
import model.decisions.CommonPlayerDecisions.*
import dsl.SpaceSyntax.*

object OpponentBehavior:
  extension (player: OpponentPlayer)
    def calculateBestDecision(matchState: MatchState, target: Option[Player]): Decision =
      val ballPlayer: Option[Player] = matchState.teams.players.find(_.hasBall)

      val ball: Ball                           = matchState.ball
      val distanceToBall: Double               = player.position.getDistance(ball.position)
      val distanceToBallPlayer: Option[Double] = ballPlayer.map(p => player.position.getDistance(p.position))
      val teamId                               = matchState.teams.teamOf(player).id

      val nextDecision: Decision = player.nextAction match
        case Action.Stopped(step) if step > 0 => player.decideConfusion(step - 1)
        case _ =>
          if distanceToBallPlayer.isDefined && distanceToBallPlayer.get < MatchConfig.tackleRange
          then player.decideTackle(ball)
          else if distanceToBall < MatchConfig.proximityRange && distanceToBallPlayer.isEmpty
          then
            if distanceToBall < MatchConfig.interceptBallRange && distanceToBallPlayer.isEmpty
            then player.decideIntercept(ball)
            else player.decideMoveToBall(player.position.getDirection(ball.position))
          else
            target.map(t => player.decideMark(t, teamId))
              .getOrElse(player.decideMoveToBall(player.position.getDirection(ball.position)))
      nextDecision

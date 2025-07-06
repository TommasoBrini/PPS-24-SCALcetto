package model.decisions.behavior
import config.MatchConfig
import model.Match.*
import model.decisions.DecisorPlayer.OpponentPlayer
import model.decisions.CommonPlayerDecisions.*
import dsl.game.TeamsSyntax.*
import dsl.game.PlayerSyntax.*
import dsl.space.PositionSyntax.*

object OpponentBehavior:
  extension (player: OpponentPlayer)
    def calculateBestDecision(state: Match, target: Option[Player]): Decision =
      val ballPlayer: Option[Player] = state.teams.players.find(_.hasBall)

      val ball: Ball                           = state.ball
      val distanceToBall: Double               = player.position distanceFrom ball.position
      val distanceToBallPlayer: Option[Double] = ballPlayer.map(p => player.position distanceFrom p.position)
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
            target.map(t => player.decideMark(t, state.teams.teamOf(player).side))
              .getOrElse(player.decideMoveToBall(player.position.getDirection(ball.position)))
      nextDecision

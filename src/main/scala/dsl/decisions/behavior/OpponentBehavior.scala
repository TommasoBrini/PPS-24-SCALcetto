package dsl.decisions.behavior

import config.MatchConfig
import model.Match.*
import dsl.decisions.PlayerTypes.*
import dsl.decisions.CommonPlayerDecisions.*
import dsl.game.TeamsSyntax.*
import dsl.game.PlayerSyntax.*
import dsl.space.PositionSyntax.*

private enum DefensiveSituation:
  case BallCarrierInTackleRange(ballCarrier: Player)
  case BallInInterceptRange
  case BallInProximityRange
  case NoImmediateThreat

object OpponentBehavior:

  /** Calculates the best decision for an opponent player based on current match state
    * @param player
    *   the opponent player making the decision
    * @param matchState
    *   the current match state
    * @param target
    *   optional target player to mark
    * @return
    *   the best decision for the player
    */
  extension (player: OpponentPlayer)
    def calculateBestDecision(matchState: Match, target: Option[Player]): Decision =
      player.nextAction match
        case Action.Stopped(remainingSteps) if remainingSteps > 0 =>
          continueConfusionState(player, remainingSteps)
        case _ =>
          selectDefensiveAction(player, matchState, target)

  private def continueConfusionState(player: Player, remainingSteps: Int): Decision =
    player.createConfusionDecision(remainingSteps - 1)

  private def selectDefensiveAction(player: OpponentPlayer, matchState: Match, target: Option[Player]): Decision =
    val situation = analyzeDefensiveSituation(player, matchState)

    situation match
      case DefensiveSituation.BallCarrierInTackleRange(ballCarrier) =>
        player.createTackleDecision(matchState.ball)
      case DefensiveSituation.BallInInterceptRange =>
        player.createInterceptDecision(matchState.ball)
      case DefensiveSituation.BallInProximityRange =>
        createMoveToBallDecision(player, matchState.ball)
      case DefensiveSituation.NoImmediateThreat =>
        createMarkingOrMoveDecision(player, matchState, target)

  private def analyzeDefensiveSituation(player: OpponentPlayer, matchState: Match): DefensiveSituation =
    val ballPlayer     = findBallCarrier(matchState)
    val distanceToBall = calculateDistanceToBall(player, matchState.ball)
    val distanceToBallCarrier = ballPlayer.map(carrier =>
      calculateDistanceToPlayer(player, carrier)
    )

    (ballPlayer, distanceToBall, distanceToBallCarrier) match
      case (Some(carrier), _, Some(distance)) if distance < MatchConfig.tackleRange =>
        DefensiveSituation.BallCarrierInTackleRange(carrier)
      case (None, distance, _) if distance < MatchConfig.proximityRange =>
        if distance < MatchConfig.interceptBallRange then
          DefensiveSituation.BallInInterceptRange
        else
          DefensiveSituation.BallInProximityRange
      case _ =>
        DefensiveSituation.NoImmediateThreat

  private def findBallCarrier(matchState: Match): Option[Player] =
    matchState.teams.players.find(_.hasBall)

  private def calculateDistanceToBall(player: Player, ball: Ball): Double =
    player.position distanceFrom ball.position

  private def calculateDistanceToPlayer(from: Player, to: Player): Double =
    from.position distanceFrom to.position

  private def createMoveToBallDecision(player: Player, ball: Ball): Decision =
    val directionToBall = player.position.getDirection(ball.position)
    player.createMoveToBallDecision(directionToBall)

  private def createMarkingOrMoveDecision(
      player: OpponentPlayer,
      matchState: Match,
      target: Option[Player]
  ): Decision =
    target match
      case Some(targetPlayer) =>
        val team = matchState.teams.teamOf(player)
        player.createMarkDecision(targetPlayer, team.side)
      case None =>
        createMoveToBallDecision(player, matchState.ball)

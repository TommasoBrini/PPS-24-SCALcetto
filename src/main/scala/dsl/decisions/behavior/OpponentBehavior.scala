package dsl.decisions.behavior

import config.MatchConfig
import model.Match.*
import dsl.decisions.PlayerTypes.*
import dsl.decisions.CommonPlayerDecisions.*
import dsl.`match`.TeamsSyntax.*
import dsl.`match`.PlayerSyntax.*
import dsl.space.PositionSyntax.*

private enum DefensiveSituation:
  case BallCarrierInTackleRange(ballCarrier: Player)
  case BallInInterceptRange
  case BallInProximityRange
  case NoImmediateThreat

object OpponentBehavior:

  extension (player: OpponentPlayer)
    /** Calculates the optimal defensive decision for an opponent player based on current match state. Implements the
      * decision-making logic for players on the team without the ball.
      *
      * @param matchState
      *   The current match state
      * @param target
      *   Optional target player to mark (from defensive assignments)
      * @return
      *   The best defensive decision for the opponent player
      */
    def calculateBestDecision(matchState: MatchState, target: Option[Player]): Decision =
      player.nextAction match
        case Action.Stopped(remainingSteps) if remainingSteps > 0 =>
          continueConfusionState(player, remainingSteps)
        case _ =>
          selectDefensiveAction(player, matchState, target)

  private def continueConfusionState(player: Player, remainingSteps: Int): Decision =
    player.createConfusionDecision(remainingSteps - 1)

  private def selectDefensiveAction(player: OpponentPlayer, matchState: MatchState, target: Option[Player]): Decision =
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

  private def analyzeDefensiveSituation(player: OpponentPlayer, matchState: MatchState): DefensiveSituation =
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

  private def findBallCarrier(matchState: MatchState): Option[Player] =
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
      matchState: MatchState,
      target: Option[Player]
  ): Decision =
    target match
      case Some(targetPlayer) =>
        val team = matchState.teams.teamOf(player)
        player.createMarkDecision(targetPlayer, team.side)
      case None =>
        createMoveToBallDecision(player, matchState.ball)

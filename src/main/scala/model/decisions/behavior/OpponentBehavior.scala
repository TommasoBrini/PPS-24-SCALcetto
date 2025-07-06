package model.decisions.behavior

import config.MatchConfig
import model.Match.*
import model.decisions.PlayerTypes.*
import model.decisions.CommonPlayerDecisions.*
import dsl.game.TeamsSyntax.*
import dsl.game.PlayerSyntax.*
import dsl.space.PositionSyntax.*

/** Represents different defensive situations
  */
private enum DefensiveSituation:
  case BallCarrierInTackleRange(ballCarrier: Player)
  case BallInInterceptRange
  case BallInProximityRange
  case NoImmediateThreat

/** Behavior implementation for opponent players
  */
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

  /** Continues the confusion state by reducing remaining steps
    * @param player
    *   the player in confusion state
    * @param remainingSteps
    *   the remaining confusion steps
    * @return
    *   a new confusion decision with reduced steps
    */
  private def continueConfusionState(player: Player, remainingSteps: Int): Decision =
    player.createConfusionDecision(remainingSteps - 1)

  /** Selects the appropriate defensive action based on current situation
    * @param player
    *   the opponent player
    * @param matchState
    *   the current match state
    * @param target
    *   optional target player to mark
    * @return
    *   the selected defensive decision
    */
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

  /** Analyzes the current defensive situation to determine action priorities
    * @param player
    *   the opponent player
    * @param matchState
    *   the current match state
    * @return
    *   the categorized defensive situation
    */
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

  /** Finds the player currently carrying the ball
    * @param matchState
    *   the current match state
    * @return
    *   optional player carrying the ball
    */
  private def findBallCarrier(matchState: Match): Option[Player] =
    matchState.teams.players.find(_.hasBall)

  /** Calculates the distance between a player and the ball
    *
    * @param player
    *   the player to measure from
    * @param ball
    *   the ball to measure to
    * @return
    *   the distance between player and ball
    */
  private def calculateDistanceToBall(player: Player, ball: Ball): Double =
    player.position distanceFrom ball.position

  /** Calculates the distance between two players
    *
    * @param from
    *   the player to measure from
    * @param to
    *   the player to measure to
    * @return
    *   the distance between the players
    */
  private def calculateDistanceToPlayer(from: Player, to: Player): Double =
    from.position distanceFrom to.position

  /** Creates a move to ball decision
    *
    * @param player
    *   the player moving to the ball
    * @param ball
    *   the ball to move towards
    * @return
    *   a move to ball decision
    */
  private def createMoveToBallDecision(player: Player, ball: Ball): Decision =
    val directionToBall = player.position.getDirection(ball.position)
    player.createMoveToBallDecision(directionToBall)

  /** Creates either a marking decision or fallback move to ball decision
    *
    * Prioritizes marking the assigned target if available, otherwise moves towards the ball as a fallback action.
    *
    * @param player
    *   the opponent player
    * @param matchState
    *   the current match state
    * @param target
    *   optional target to mark
    * @return
    *   either a mark decision or move to ball decision
    */
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

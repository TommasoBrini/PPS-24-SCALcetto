package dsl.decisions.behavior

import config.MatchConfig
import model.Match.*
import model.Match.Action.Stopped
import model.Match.Decision.MoveRandom
import dsl.decisions.PlayerTypes.*
import dsl.decisions.DecisionGenerator.*
import dsl.decisions.CommonPlayerDecisions.*
import scala.util.Random
import dsl.space.PositionSyntax.*
import dsl.`match`.TeamsSyntax.*
import dsl.`match`.PlayerSyntax.*

private enum TeammateSituation:
  case Confusion(remainingSteps: Int)
  case BallInInterceptRange
  case BallInProximityRange
  case ContinueMovement(direction: Direction, remainingSteps: Int)
  case RandomMovement

object TeammateBehavior:

  extension (player: TeammatePlayer)
    /** Calculates the optimal support decision for a teammate player based on current match state. Implements the
      * decision-making logic for players on the team with the ball
      *
      * @param state
      *   The current match state
      * @return
      *   The best support decision for the teammate player
      */
    def calculateBestDecision(state: MatchState): Decision =
      val situation = analyzeSituation(player, state)
      takeDecision(player, situation, state)

  private def analyzeSituation(player: TeammatePlayer, state: MatchState): TeammateSituation =
    player.nextAction match
      case Stopped(steps) if steps > 0 => TeammateSituation.Confusion(steps - 1)
      case _ =>
        val distanceToBall = player.position distanceFrom state.ball.position

        if distanceToBall < MatchConfig.interceptBallRange then
          TeammateSituation.BallInInterceptRange
        else if isBallInProximityAndNoOneHasBall(distanceToBall, player, state) then
          TeammateSituation.BallInProximityRange
        else
          analyzeRandomMovementSituation(player)

  private def isBallInProximityAndNoOneHasBall(
      distanceToBall: Double,
      player: TeammatePlayer,
      state: MatchState
  ): Boolean =
    distanceToBall < MatchConfig.proximityRange && !state.teams.players.exists(_.hasBall)

  private def analyzeRandomMovementSituation(player: TeammatePlayer): TeammateSituation =
    player.decision match
      case MoveRandom(direction, steps) if steps > 0 =>
        TeammateSituation.ContinueMovement(direction, steps - 1)
      case _ =>
        TeammateSituation.RandomMovement

  private def takeDecision(player: TeammatePlayer, situation: TeammateSituation, state: MatchState): Decision =
    situation match
      case TeammateSituation.Confusion(remainingSteps) =>
        player.createConfusionDecision(remainingSteps)

      case TeammateSituation.BallInInterceptRange =>
        player.createReceivePassDecision(state.ball)

      case TeammateSituation.BallInProximityRange =>
        val directionToBall = player.position.getDirection(state.ball.position)
        player.createMoveToBallDecision(directionToBall)

      case TeammateSituation.ContinueMovement(direction, remainingSteps) =>
        player.createRandomMovementDecision(direction, remainingSteps)

      case TeammateSituation.RandomMovement =>
        val randomDirection = generateRandomDirection()
        player.createRandomMovementDecision(randomDirection, MatchConfig.moveRandomSteps)

  private def generateRandomDirection(): Direction =
    Direction(Random.between(-1.toDouble, 1), Random.between(-1.toDouble, 1))

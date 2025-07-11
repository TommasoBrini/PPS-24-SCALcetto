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
import dsl.game.TeamsSyntax.*
import dsl.game.PlayerSyntax.*

/** Represents different teammate situations that require specific decisions
  */
private enum TeammateSituation:
  case Confusion(remainingSteps: Int)
  case BallInInterceptRange
  case BallInProximityRange
  case ContinueMovement(direction: Direction, remainingSteps: Int)
  case RandomMovement

object TeammateBehavior:

  /** Calculates the best decision for a teammate player based on current match state
    * @param player
    *   the teammate player making the decision
    * @param state
    *   the current match state
    * @return
    *   the best decision for the player
    */
  extension (player: TeammatePlayer)
    def calculateBestDecision(state: Match): Decision =
      val situation = analyzeSituation(player, state)
      takeDecision(player, situation, state)

  private def analyzeSituation(player: TeammatePlayer, state: Match): TeammateSituation =
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
      state: Match
  ): Boolean =
    distanceToBall < MatchConfig.proximityRange && !state.teams.players.exists(_.hasBall)

  private def analyzeRandomMovementSituation(player: TeammatePlayer): TeammateSituation =
    player.decision match
      case MoveRandom(direction, steps) if steps > 0 =>
        TeammateSituation.ContinueMovement(direction, steps - 1)
      case _ =>
        TeammateSituation.RandomMovement

  private def takeDecision(player: TeammatePlayer, situation: TeammateSituation, state: Match): Decision =
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

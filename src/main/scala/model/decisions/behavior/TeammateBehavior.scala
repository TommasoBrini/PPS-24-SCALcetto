package model.decisions.behavior
import config.MatchConfig
import model.Match.*
import model.Match.Action.Stopped
import model.Match.Decision.MoveRandom
import model.decisions.PlayerTypes.*
import model.decisions.DecisionGenerator.*
import model.decisions.CommonPlayerDecisions.*
import scala.util.Random
import dsl.space.PositionSyntax.*
import dsl.game.TeamsSyntax.*
import dsl.game.PlayerSyntax.*

object TeammateBehavior:
  extension (player: TeammatePlayer)
    def calculateBestDecision(state: MatchState): Decision =
      player.nextAction match
        case Stopped(steps) if steps > 0 => player.createConfusionDecision(steps - 1)
        case _ =>
          if (player.position distanceFrom state.ball.position) < MatchConfig.interceptBallRange
          then player.createReceivePassDecision(state.ball)
          else if (player.position distanceFrom
              state.ball.position) < MatchConfig.proximityRange && !state.teams.players.exists(_.hasBall)
          then player.createMoveToBallDecision(player.position.getDirection(state.ball.position))
          else
            player.decision match
              case MoveRandom(direction, steps) if steps > 0 =>
                player.createRandomMovementDecision(direction, steps - 1)
              case _ =>
                val direction = Direction(Random.between(-1.toDouble, 1), Random.between(-1.toDouble, 1))
                player.createRandomMovementDecision(direction, MatchConfig.moveRandomSteps)

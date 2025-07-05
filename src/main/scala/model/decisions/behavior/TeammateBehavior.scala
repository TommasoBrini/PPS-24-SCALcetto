package model.decisions.behavior
import config.MatchConfig
import model.Match.*
import model.Match.Action.Stopped
import model.Match.Decision.MoveRandom
import model.decisions.DecisorPlayer.TeammatePlayer
import model.decisions.PossibleDecisionFactory.*
import model.decisions.CommonPlayerDecisions.*
import scala.util.Random
import dsl.SpaceSyntax.*

object TeammateBehavior:
  extension (player: TeammatePlayer)
    def calculateBestDecision(state: Match): Decision =
      player.nextAction match
        case Stopped(steps) if steps > 0 => player.decideConfusion(steps - 1)
        case _ =>
          if player.position.getDistance(state.ball.position) < MatchConfig.interceptBallRange
          then player.decideReceivePass(state.ball)
          else if player.position.getDistance(
              state.ball.position
            ) < MatchConfig.proximityRange && !state.teams.players.exists(_.hasBall)
          then player.decideMoveToBall(player.position.getDirection(state.ball.position))
          else
            player.decision match
              case MoveRandom(direction, steps) if steps > 0 => player.decideMoveRandom(direction, steps - 1)
              case _ =>
                val direction = Direction(Random.between(-1.toDouble, 1), Random.between(-1.toDouble, 1))
                player.decideMoveRandom(direction, MatchConfig.moveRandomSteps)

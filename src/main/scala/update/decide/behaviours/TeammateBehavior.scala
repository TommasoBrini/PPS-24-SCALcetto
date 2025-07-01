package update.decide.behaviours
import config.MatchConfig
import model.Match.*
import model.Match.Action.Stopped
import model.player.Player
import model.Match.Decision.MoveRandom
import model.decisions.DecisorPlayer
import model.decisions.CommonPlayerDecisions.*

import scala.util.Random

object TeammateBehavior extends PlayerBehavior:
  def decide(player: Player, state: MatchState): Decision = player match
    case teammate: DecisorPlayer.TeammatePlayer =>
      if state.ball.isHeadingToward(player, MatchConfig.passDirectionRange) then
        if teammate.position.getDistance(state.ball.position) < MatchConfig.interceptBallRange
        then teammate.decideReceivePass(state.ball)
        else teammate.decideMoveToBall(teammate.position.getDirection(state.ball.position))
      else
        teammate.nextAction match
          case Stopped(steps) if steps > 0 => teammate.decideConfusion(steps - 1)
          case _ =>
            teammate.decision match
              case MoveRandom(dir, steps) if steps > 0 => teammate.decideMoveRandom(dir, steps - 1)
              case _ =>
                val direction = Direction(Random.between(-1.toDouble, 1), Random.between(-1.toDouble, 1))
                teammate.decideMoveRandom(direction, MatchConfig.moveRandomSteps)
    case _ =>
      throw new IllegalArgumentException("TeammateBehavior can only be used with TeammatePlayer instances")

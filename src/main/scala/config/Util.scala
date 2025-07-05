package config

import dsl.SpaceSyntax.*
import model.Match.MatchState
import model.Space.{Direction, Position}
import model.Match.Player
import scala.collection.IterableOnce.*

object Util:

  def assignMarkings(defenders: List[Player], attackers: List[Player]): Map[Player, Player] =
    val (withBallOpt, others) = attackers.partition(_.hasBall) match
      case (ballCarrier :: Nil, rest) => (Some(ballCarrier), rest)
      case _                          => (None, attackers)
    var unassignedAttackers = others.toSet
    var availableDefenders  = defenders.sortBy(_.id)
    var markings            = Map.empty[Player, Player]
    withBallOpt.foreach { ballCarrier =>
      val maybeDefender = availableDefenders.minByOption(_.position.getDistance(ballCarrier.position))
      maybeDefender.foreach { defender =>
        markings += (defender -> ballCarrier)
        availableDefenders = availableDefenders.filterNot(_ == defender)
      }
    }
    availableDefenders.foreach { defender =>
      val maybeTarget = unassignedAttackers.minByOption(_.position.getDistance(defender.position))
      maybeTarget.foreach { target =>
        markings += (defender -> target)
        unassignedAttackers -= target
      }
    }
    markings

  def isPathClear(from: Position, to: Position, state: MatchState, teamId: Int): Boolean =
    val opponents: List[Player] = (state.teams opponentOf teamId).players
    opponents.forall { opponent =>
      !positionIsInBetween(from, to, opponent.position)
    }

  def positionIsInBetween(start: Position, end: Position, mid: Position): Boolean =
    val dx1       = end.x - start.x
    val dy1       = end.y - start.y
    val dx2       = mid.x - start.x
    val dy2       = mid.y - start.y
    val cross     = dx1 * dy2 - dy1 * dx2
    val collinear = cross == 0
    val inSegment =
      (mid.x >= Math.min(start.x, end.x) && mid.x <= Math.max(start.x, end.x)) &&
        (mid.y >= Math.min(start.y, end.y) && mid.y <= Math.max(start.y, end.y))
    collinear && inSegment

  def isDirectionClear(from: Position, dir: Direction, state: MatchState): Boolean =
    val opponents = state.teams.players.filterNot(_.hasBall)
    !opponents.exists { opponent =>
      val dx    = opponent.position.x - from.x
      val dy    = opponent.position.y - from.y
      val dot   = dx * dir.x + dy * dir.y
      val cross = dx * dir.y - dy * dir.x
      math.abs(cross) < 1e-6 && dot > 0
    }

package config

import model.Match.MatchState
import model.Space.{Direction, Position}
import model.Match.Player

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

  def isPathClear(from: Position, dir: Direction, state: MatchState): Boolean =
    val sideRange: Int     = 15
    val verticalRange: Int = 15
    val opponents = state.teams
      .flatMap(_.players)
      .filterNot(_.hasBall)
    opponents.forall { opponent =>
      val dx               = opponent.position.x - from.x
      val dy               = opponent.position.y - from.y
      val projectedForward = dx * dir.x + dy * dir.y
      val projectedSide    = math.abs(-dx * dir.y + dy * dir.x)
      !(projectedForward >= 0 && projectedForward <= verticalRange && projectedSide <= sideRange)
    }

  def positionIsInBetween(start: Position, end: Position, mid: Position): Boolean =
    MatchConfig.tackleRange > Math.abs(start.getDistance(end) - start.getDistance(mid) + mid.getDistance(end))

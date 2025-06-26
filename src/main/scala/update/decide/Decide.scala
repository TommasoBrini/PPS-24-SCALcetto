package update.decide

import model.Match.*
import model.player.Player
import update.decide.behaviours.*

object Decide:

  def decide(state: MatchState): MatchState =
    state.teams match
      case List(teamA, teamB) =>
        val (defenders, attackers) =
          if teamA.hasBall then (teamB, teamA)
          else (teamA, teamB)

        val markings: Map[Player, Player] = assignMarkings(defenders.players, attackers.players)

        val updatedTeams: List[Team] = state.teams.map { team =>

          val newPlayers = team.players.map { player =>
            val behavior: PlayerBehavior = player match
              case _: Player.ControlPlayer  => ControlPlayerBehavior
              case _: Player.OpponentPlayer => OpponentBehavior(markings.get(player))
              case _: Player.TeammatePlayer => TeammateBehavior
              case _                        => DefaultBehavior
            player.copy(decision = behavior.decide(player, state))
          }

          team.copy(players = newPlayers)
        }

        state.copy(teams = updatedTeams)

      case _ =>
        throw new IllegalArgumentException("MatchState must contain exactly two teams.")

  private def assignMarkings(defenders: List[Player], attackers: List[Player]): Map[Player, Player] =
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

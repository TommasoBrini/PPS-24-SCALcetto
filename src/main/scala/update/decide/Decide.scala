package update.decide

import config.Util
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

        val markings: Map[Player, Player] = Util.assignMarkings(defenders.players, attackers.players)

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

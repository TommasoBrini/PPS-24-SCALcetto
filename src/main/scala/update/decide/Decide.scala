package update.decide

import config.Util
import model.Match.*
import model.decisions.DecisorPlayer
import model.decisions.CommonPlayerDecisions.*
import model.decisions.behaviors.Decisor.*

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
            player.copy(decision = player.decide(state))
          }
          team.copy(players = newPlayers)
        }

        state.copy(teams = updatedTeams)

      case _ =>
        throw new IllegalArgumentException("MatchState must contain exactly two teams.")

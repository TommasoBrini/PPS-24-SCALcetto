package update.decide

import config.Util
import model.Match.*
import model.decisions.DecisionMaker.*

object Decide:

  def decide(state: MatchState): MatchState =
    state.teams match
      case List(teamA, teamB) =>
        val (defenders, attackers) =
          if teamA.hasBall then (teamB, teamA)
          else (teamA, teamB)

        val markings = Util.assignMarkings(defenders.players, attackers.players)

        val newTeams = List(teamA, teamB).map { team =>
          val newPlayers = team.players.map(player =>
            player.copy(decision = player.decide(state, markings))
          )
          team.copy(players = newPlayers)
        }

        state.copy(teams = newTeams)
      case _ =>
        throw new IllegalArgumentException("MatchState must contain exactly two teams.")

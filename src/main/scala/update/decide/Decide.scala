package update.decide

import config.Util
import model.Match.*
import model.decisions.DecisionMaker.*

object Decide:

  def decide(state: MatchState): MatchState =
    state.teams match
      case (teamA, teamB) =>
        val (defenders, attackers) =
          if teamA.hasBall then (teamB, teamA)
          else (teamA, teamB)

        val markings = Util.assignMarkings(defenders.players, attackers.players)

        // TODO do some dry here
        val playersADecided: List[Player] = teamA.players.map(player =>
          player.copy(decision = player.decide(state, markings))
        )
        val newTeamA: Team = teamA.copy(players = playersADecided)

        val playersBDecided: List[Player] = teamB.players.map(player =>
          player.copy(decision = player.decide(state, markings))
        )
        val newTeamB: Team = teamB.copy(players = playersBDecided)

        state.copy(teams = (newTeamA, newTeamB))
      case _ =>
        throw new IllegalArgumentException("MatchState must contain exactly two teams.")

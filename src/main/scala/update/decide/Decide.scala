package update.decide

import config.Util
import model.Match.*
import dsl.decisions.DecisionMaker.*
import monads.States.*
import dsl.game.TeamsSyntax.*
import dsl.game.PlayerSyntax.*
import dsl.decisions.PlayerRoleFactory.*

object Decide:

  /** Orchestrates the decision-making process for all players in the match.
    *
    * This function coordinates the entire decision-making workflow: assign roles, determine team roles, assign markings
    * and update all players with new decisions.
    *
    * @return
    *   A State monad that transforms the match state by updating all player decisions
    */
  def decideStep: State[Match, Unit] =
    State(state =>
      (
        state.teams.map(assignRoles) match
          case (teamA, teamB) =>
            val (defenders, attackers) = determineTeamRoles(teamA, teamB)
            val markings               = Util.assignMarkings(defenders.players, attackers.players)
            val updatedTeams           = updateBothTeams(teamA, teamB, state, markings)
            (state.copy(teams = updatedTeams), ())
      )
    )

  private def determineTeamRoles(teamA: Team, teamB: Team): (Team, Team) =
    if teamA.hasBall then (teamB, teamA) else (teamA, teamB)

  private def updateBothTeams(
      teamA: Team,
      teamB: Team,
      state: Match,
      markings: Map[Player, Player]
  ): (Team, Team) =
    val updatedTeamA = updateTeamDecisions(teamA, state, markings)
    val updatedTeamB = updateTeamDecisions(teamB, state, markings)
    (updatedTeamA, updatedTeamB)

  private def updateTeamDecisions(team: Team, state: Match, markings: Map[Player, Player]): Team =
    val updatedPlayers = team.players.map(player => updatePlayerDecision(player, state, markings))
    team.copy(players = updatedPlayers)

  private def updatePlayerDecision(player: Player, state: Match, markings: Map[Player, Player]): Player =
    player.copy(decision = player.decide(state, markings))

  private def assignRoles(team: Team): Team =
    team.copy(players =
      team.players.map(p =>
        if p.hasBall then p.asBallCarrierPlayer
        else if team.hasBall then p.asTeammatePlayer
        else p.asOpponentPlayer
      )
    )

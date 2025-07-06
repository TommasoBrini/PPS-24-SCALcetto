package update.decide

import config.Util
import model.Match.*
import model.decisions.DecisionMaker.*
import monads.States.*
import dsl.game.TeamsSyntax.*
import dsl.game.PlayerSyntax.*
import model.decisions.PlayerRoleFactory.*

object Decide:

  /** Main decision function that orchestrates the decision-making process for all players
    */
  def decideStep: State[Match, Unit] =
    State(s => (decide(s), ()))

  def decide(state: Match): Match =
    state.teams.map(assignRoles) match
      case (teamA, teamB) =>
        val (defenders, attackers) = determineTeamRoles(teamA, teamB)
        val markings               = Util.assignMarkings(defenders.players, attackers.players)
        val updatedTeams           = updateBothTeams(teamA, teamB, state, markings)
        state.copy(teams = updatedTeams)

  /** Determines which team has the ball and returns them as (defenders, attackers)
    */
  private def determineTeamRoles(teamA: Team, teamB: Team): (Team, Team) =
    if teamA.hasBall then (teamB, teamA) else (teamA, teamB)

  /** Updates both teams with new player decisions */
  private def updateBothTeams(
      teamA: Team,
      teamB: Team,
      state: Match,
      markings: Map[Player, Player]
  ): (Team, Team) =
    val updatedTeamA = updateTeamDecisions(teamA, state, markings)
    val updatedTeamB = updateTeamDecisions(teamB, state, markings)
    (updatedTeamA, updatedTeamB)

  /** Updates all players in a team with new decisions
    */
  private def updateTeamDecisions(team: Team, state: Match, markings: Map[Player, Player]): Team =
    val updatedPlayers = team.players.map(player => updatePlayerDecision(player, state, markings))
    team.copy(players = updatedPlayers)

  /** Updates a single player's decision based on the current state and markings
    */
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

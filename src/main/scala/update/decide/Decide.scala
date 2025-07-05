package update.decide

import config.Util
import model.Match.*
import model.decisions.DecisionMaker.*

object Decide:

  /** Main decision function that orchestrates the decision-making process for all players
    * @param state
    *   the current state of the match
    * @return
    *   the updated state of the match
    */
  def decide(state: MatchState): MatchState =
    state.teams match
      case (teamA, teamB) =>
        val (defenders, attackers) = determineTeamRoles(teamA, teamB)
        val markings               = Util.assignMarkings(defenders.players, attackers.players)
        val updatedTeams           = updateBothTeams(teamA, teamB, state, markings)
        state.copy(teams = updatedTeams)

  /** Determines which team has the ball and returns them as (defenders, attackers)
    * @param teamA
    *   the first team
    * @param teamB
    *   the second team
    * @return
    *   the updated teams
    */
  private def determineTeamRoles(teamA: Team, teamB: Team): (Team, Team) =
    if teamA.hasBall then (teamB, teamA) else (teamA, teamB)

  /** Updates both teams with new player decisions
    * @param teamA
    *   the first team
    * @param teamB
    *   the second team
    * @param state
    *   the current state of the match
    * @param markings
    *   the markings of the players
    * @return
    *   the updated teams
    */
  private def updateBothTeams(
      teamA: Team,
      teamB: Team,
      state: MatchState,
      markings: Map[Player, Player]
  ): (Team, Team) =
    val updatedTeamA = updateTeamDecisions(teamA, state, markings)
    val updatedTeamB = updateTeamDecisions(teamB, state, markings)
    (updatedTeamA, updatedTeamB)

  /** Updates all players in a team with new decisions
    * @param team
    *   the team to update
    * @param state
    *   the current state of the match
    * @param markings
    *   the markings of the players
    * @return
    *   the updated team
    */
  private def updateTeamDecisions(team: Team, state: MatchState, markings: Map[Player, Player]): Team =
    val updatedPlayers = team.players.map(player => updatePlayerDecision(player, state, markings))
    team.copy(players = updatedPlayers)

  /** Updates a single player's decision based on the current state and markings
    * @param player
    *   the player to update
    * @param state
    *   the current state of the match
    * @param markings
    *   the markings of the players
    * @return
    *   the updated player
    */
  private def updatePlayerDecision(player: Player, state: MatchState, markings: Map[Player, Player]): Player =
    player.copy(decision = player.decide(state, markings))

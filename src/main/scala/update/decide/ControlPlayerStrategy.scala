package update.decide

import model.Match.{Decision, MatchState, Player, Team}

object ControlPlayerStrategy extends DecisionStrategy:

  private def getClosestTeammate(ballPlayer: Player, teams: List[Team]): Player =
    teams.filter(_.players.contains(ballPlayer)).head.players.filter(_.id != ballPlayer.id).head

  private def possiblePasses(player: Player, state: MatchState): List[Decision] =
    for
      team     <- state.teams.filter(_.players.contains(player))
      teammate <- team.players.filter(!_.equals(player))
    yield Decision.Pass(player, teammate)

  private def possibleMoves(player: Player, matchState: MatchState): List[Decision] = Nil // todo

  private def possibleShots(player: Player, matchState: MatchState): List[Decision] = Nil // todo

  private def calculateBestAction(player: Player, state: MatchState): Decision =
    val possibleActions = possiblePasses(player, state) ++
      possibleMoves(player, state) ++ possibleShots(player, state)
    type Rating = Double
    val decisionRatings: Map[Decision, Rating] = possibleActions
      .map(decision => (decision, calculateActionRating(decision, player, state))).toMap
    decisionRatings.maxBy(_._2)._1

  private def calculateActionRating(playerDecision: Decision, player: Player, state: MatchState): Double =
    playerDecision match
      case Decision.Pass(from, to)             => 1 / from.position.getDistance(to.position)
      case Decision.Shoot(striker, goal)       => ??? // todo
      case Decision.MoveToGoal(attacker, goal) => ??? // todo

  def decide(player: Player, state: MatchState): Decision =
    calculateBestAction(player, state)

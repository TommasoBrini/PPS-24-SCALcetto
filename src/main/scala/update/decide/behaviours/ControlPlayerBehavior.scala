package update.decide.behaviours
import config.UIConfig
import config.MatchConfig
import config.Util
import model.Match.*
import model.player.Player
import model.decisions.PlayerDecisionFactory.*
import model.decisions.DecisorPlayer
import model.decisions.Decisioner.possibleDecision
import model.decisions.CommonPlayerDecisions.*

object ControlPlayerBehavior extends PlayerBehavior:
  def decide(player: Player, matchState: MatchState): Decision = player match
    case controlPlayer: DecisorPlayer.ControlPlayer =>
      val decision = calculateBestAction(controlPlayer, matchState)
      println(s"ControlPlayerBehavior: $decision")
      decision
    case _ =>
      throw new IllegalArgumentException("ControlPlayerBehavior can only be used with ControlPlayer instances")

  private def calculateBestAction(player: Player, state: MatchState): Decision =
    val possibleActions =
      if player.decision == Decision.Initial
      then
        player.asControlDecisionPlayer.possiblePasses(state)
      else
        player.asControlDecisionPlayer.possibleDecision(state)

    type Rating = Double
    val decisionRatings: Map[Decision, Rating] = possibleActions
      .map(decision => (decision, calculateActionRating(decision, player, state))).toMap
    decisionRatings.maxBy(_._2)._1

  private def positionIsInBetween(start: Position, end: Position, mid: Position): Boolean =
    MatchConfig.tackleRange > Math.abs(start.getDistance(end) - start.getDistance(mid) + mid.getDistance(end))

  private def shootRating(striker: Player, state: MatchState, goal: Position): Double =
    val opponentsInBetween: List[Player] = state.teams
      .flatMap(_.players)
      .filterNot(_.hasBall)
      .filter(opp => positionIsInBetween(striker.position, goal, opp.position))
    val shootRating: Double = striker.position.getDistance(goal) match
      case dist if dist <= MatchConfig.lowDistanceShoot  => 3 // todo change this values next meeting
      case dist if dist <= MatchConfig.midDistanceShoot  => 0.70
      case dist if dist <= MatchConfig.highDistanceShoot => 0.30
      case _                                             => 0.0
    if opponentsInBetween.isEmpty
    then shootRating
    else 0.0

  private def calculateActionRating(playerDecision: Decision, player: Player, state: MatchState): Double =
    playerDecision match
      case Decision.Pass(from, to)        => 1 / from.position.getDistance(to.position)
      case Decision.Shoot(striker, goal)  => shootRating(striker, state, goal)
      case Decision.MoveToGoal(direction) => moveToGoalRating(player, direction, state)
      case Decision.Run(direction)        => runRating(player, direction, state)
      case _                              => 0

  private def moveToGoalRating(player: Player, goalDirection: Direction, state: MatchState): Double =
    if Util.isPathClear(player.position, goalDirection, state) then 0.9 else 0.0

  private def runRating(player: Player, dir: Direction, state: MatchState): Double =
    0 // todo

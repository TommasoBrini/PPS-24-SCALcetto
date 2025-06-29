package update.decide.behaviours
import config.{FieldConfig, Util}
import model.Match.*
import model.player.Player
import model.player.possibleDecision

object ControlPlayerBehavior extends PlayerBehavior:
  def decide(player: Player, matchState: MatchState): Decision = player match
    case controlPlayer: Player.ControlPlayer =>
      calculateBestAction(controlPlayer, matchState)
    case _ =>
      throw new IllegalArgumentException("ControlPlayerBehavior can only be used with ControlPlayer instances")

  private def calculateBestAction(player: Player, state: MatchState): Decision =
    val possibleActions =
      if player.decision == Decision.Initial
      then possiblePasses(player.asControlPlayer, state)
      else
        possiblePasses(player.asControlPlayer, state) ++
          possibleMoves(player.asControlPlayer, state) ++ player.possibleDecision(state)

    type Rating = Double
    val decisionRatings: Map[Decision, Rating] = possibleActions
      .map(decision => (decision, calculateActionRating(decision, player, state))).toMap
    decisionRatings.maxBy(_._2)._1

  private def possiblePasses(player: Player.ControlPlayer, state: MatchState): List[Decision] =
    for
      team     <- state.teams.filter(_.players.contains(player))
      teammate <- team.players.filter(!_.equals(player))
    yield player.decidePass(teammate)

  private[update] def possibleMoves(player: Player.ControlPlayer, matchState: MatchState): List[Decision] =
    val goalPosition: Position =
      if matchState.teams.head.players.contains(player)
      then Position(FieldConfig.fieldWidth * FieldConfig.scale, (FieldConfig.fieldHeight * FieldConfig.scale) / 2)
      else Position(0, (FieldConfig.fieldHeight * FieldConfig.scale) / 2)
    val goalDirection = player.decideMoveToGoal(player.position.getDirection(goalPosition))
    val runDirections =
      for
        dx <- -1 to 0
        dy <- -1 to 1
        if dx != 0 || dy != 0
      yield player.decideRun(Direction(dx, dy))
    goalDirection :: runDirections.toList

  private def distanceBetweenPoints(start: Position, end: Position): Double =
    Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2))

  private def positionIsInBetween(start: Position, end: Position, mid: Position): Boolean =
    FieldConfig.tackleRange > Math.abs(distanceBetweenPoints(start, end) - distanceBetweenPoints(
      start,
      mid
    ) + distanceBetweenPoints(mid, end))

  private def shootRating(striker: Player, state: MatchState, goal: Position): Double =
    val opponentsInBetween: List[Player] = state.teams
      .flatMap(_.players)
      .filterNot(_.hasBall)
      .filter(opp => positionIsInBetween(striker.position, goal, opp.position))
    val shootRating: Double = striker.position.getDistance(goal) match
      case dist if dist <= FieldConfig.lowDistanceShoot  => 3 // todo change this values next meeting
      case dist if dist <= FieldConfig.midDistanceShoot  => 0.70
      case dist if dist <= FieldConfig.highDistanceShoot => 0.30
      case _                                             => 0.0
    if opponentsInBetween.isEmpty
    then shootRating
    else 0.0

  private def calculateActionRating(playerDecision: Decision, player: Player, state: MatchState): Double =
    playerDecision match
      case Decision.Pass(from, to)        => 1 / from.position.getDistance(to.position)
      case Decision.Shoot(striker, goal)  => shootRating(striker, state, goal)
      case Decision.MoveToGoal(direction) => 1 // moveToGoalRating(player, direction, state)
      case Decision.Run(direction)        => runRating(player, direction, state)
      case _                              => 0

  private def moveToGoalRating(player: Player, goalDirection: Direction, state: MatchState): Double =
    if Util.isPathClear(player.position, goalDirection, state) then 0.9 else 0.0

  private def runRating(player: Player, dir: Direction, state: MatchState): Double =
    0 // todo

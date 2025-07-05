package update.decide.behaviours
import config.UIConfig
import config.MatchConfig
import model.Match.*

object ControlPlayerBehavior extends PlayerBehavior:
  def decide(player: Player, matchState: Match): Decision =
    calculateBestAction(player, matchState)

  private def calculateBestAction(player: Player, state: Match): Decision =
    val possibleActions =
      if player.decision == Decision.Initial
      then possiblePasses(player, state)
      else
        possiblePasses(player, state) ++
          possibleMoves(player, state) ++ possibleShots(player, state)

    type Rating = Double
    val decisionRatings: Map[Decision, Rating] = possibleActions
      .map(decision => (decision, calculateActionRating(decision, player, state))).toMap
    decisionRatings.maxBy(_._2)._1

  private def possiblePasses(player: Player, state: Match): List[Decision] =
    for
      team     <- state.teams.filter(_.players.contains(player))
      teammate <- team.players.filter(!_.equals(player))
    yield Decision.Pass(player, teammate)

  private[update] def possibleMoves(player: Player, matchState: Match): List[Decision] =
    val goalPosition: Position =
      if matchState.teams.head.players.contains(player)
      then Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
      else Position(0, UIConfig.fieldHeight / 2)
    val goalDirection = Decision.MoveToGoal(player.position.getDirection(goalPosition))
    val runDirections =
      for
        dx <- -1 to 0
        dy <- -1 to 1
        if dx != 0 || dy != 0
      yield Decision.Run(Direction(dx, dy))
    goalDirection :: runDirections.toList

  private def positionIsInBetween(start: Position, end: Position, mid: Position): Boolean =
    MatchConfig.tackleRange > Math.abs(start.getDistance(end) - start.getDistance(mid) + mid.getDistance(end))

  private def possibleShots(player: Player, matchState: Match): List[Decision] =
    val goalX: Int =
      if matchState.teams.head.players.contains(player)
      then UIConfig.goalEastX
      else UIConfig.goalWestX

    val goalPositions: List[Position] = List(
      Position(goalX, UIConfig.firstPoleY),
      Position(goalX, UIConfig.midGoalY),
      Position(goalX, UIConfig.secondPoleY)
    )
    goalPositions.map(Decision.Shoot(player, _))

  private def shootRating(striker: Player, state: Match, goal: Position): Double =
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

  private def calculateActionRating(playerDecision: Decision, player: Player, state: Match): Double =
    playerDecision match
      case Decision.Pass(from, to)        => 1 / from.position.getDistance(to.position)
      case Decision.Shoot(striker, goal)  => shootRating(striker, state, goal)
      case Decision.MoveToGoal(direction) => moveToGoalRating(player, direction, state)
      case Decision.Run(direction)        => runRating(player, direction, state)
      case _                              => 0

  private def moveToGoalRating(player: Player, goalDirection: Direction, state: Match): Double =
    if pathClear(player.position, goalDirection, state) then 0.9 else 0.0

  private def runRating(player: Player, dir: Direction, state: Match): Double =
    0 // todo

  private def pathClear(from: Position, dir: Direction, state: Match): Boolean =
    val sideRange: Int     = 15
    val verticalRange: Int = 15

    val opponents = state.teams
      .flatMap(_.players)
      .filterNot(_.hasBall)

    opponents.forall { opponent =>
      val dx = opponent.position.x - from.x
      val dy = opponent.position.y - from.y

      val projectedForward = dx * dir.x + dy * dir.y
      val projectedSide    = math.abs(-dx * dir.y + dy * dir.x)

      !(projectedForward >= 0 && projectedForward <= verticalRange && projectedSide <= sideRange)
    }

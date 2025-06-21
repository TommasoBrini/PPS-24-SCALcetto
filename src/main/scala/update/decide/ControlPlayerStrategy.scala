package update.decide

import config.FieldConfig
import model.Match.{Decision, MatchState, Player, Team}
import model.Space.{Direction, Position}

object ControlPlayerStrategy extends DecisionStrategy:

  def decide(player: Player, state: MatchState): Decision =
    calculateBestAction(player, state)

  private def calculateBestAction(player: Player, state: MatchState): Decision =
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

  private def possiblePasses(player: Player, state: MatchState): List[Decision] =
    for
      team     <- state.teams.filter(_.players.contains(player))
      teammate <- team.players.filter(!_.equals(player))
    yield Decision.Pass(player, teammate)

  private[update] def possibleMoves(player: Player, matchState: MatchState): List[Decision] =
    val goalPosition: Position =
      if matchState.teams.head.players.contains(player)
      then Position(FieldConfig.fieldWidth * FieldConfig.scale, (FieldConfig.fieldHeight * FieldConfig.scale) / 2)
      else Position(0, (FieldConfig.fieldHeight * FieldConfig.scale) / 2)
    val toGoalDecision = Decision.MoveToGoal(player, goalPosition)
    val runDirections =
      for
        dx <- -1 to 0
        dy <- -1 to 1
        if dx != 0 || dy != 0
      yield Decision.Run(Direction(dx, dy))
    toGoalDecision :: runDirections.toList

  private def possibleShots(player: Player, matchState: MatchState): List[Decision] = Nil // todo

  private def calculateActionRating(playerDecision: Decision, player: Player, state: MatchState): Double =
    playerDecision match
      case Decision.Pass(from, to)           => 1 / from.position.getDistance(to.position)
      case Decision.Shoot(striker, goal)     => ??? // todo
      case Decision.MoveToGoal(player, goal) => moveToGoalRating(player, goal, state)
      case Decision.Run(direction)           => runRating(player, direction, state)
      case _                                 => 0

  private def moveToGoalRating(player: Player, goalPos: Position, state: MatchState): Double =
    if pathClear(player.position, goalPos, state) then 1.0 else 0.0

  private def runRating(player: Player, dir: Direction, state: MatchState): Double =
    0

  private def pathClear(from: Position, to: Position, state: MatchState): Boolean =
    val sideRange: Int     = 15
    val verticalRange: Int = 15

    val opponents = state.teams
      .flatMap(_.players)
      .filterNot(_.hasBall)

    opponents.forall { opponent =>
      val dx = opponent.position.x - from.x
      val dy = opponent.position.y - from.y

      val dir              = from.getDirection(to)
      val projectedForward = dx * dir.x + dy * dir.y
      val projectedSide    = math.abs(-dx * dir.y + dy * dir.x)

      !(projectedForward >= 0 && projectedForward <= verticalRange && projectedSide <= sideRange)
    }

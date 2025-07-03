package model.decisions.rating
import model.Match.*
import config.MatchConfig
import config.Util
import config.UIConfig

object ControlDecisionRating:
  extension (run: Decision.Run)
    def rate(player: Player, state: MatchState): Double =
      if Util.isDirectionClear(player.position, run.direction, state) && player.decision != Decision.Initial
      then 0.4
      else 0.0

  extension (pass: Decision.Pass)
    def rate(state: MatchState): Double =
      val from   = pass.from.position
      val to     = pass.to.position
      val teamId = if state.teams.head.players.contains(pass.from) then state.teams.head.id else state.teams.last.id
      val advancement = if state.teams.head.players.contains(pass.from) then to.x - from.x else from.x - to.x
      val pathClear   = Util.isPathClear(from, to, state, teamId)
      if !pathClear then 0.0
      else
        val distance         = from.getDistance(to)
        val advancementScore = Math.max(0.0, advancement / UIConfig.fieldWidth)
        val distanceScore    = if distance < 5 then 0.2 else 1.0 / distance
        0.6 * advancementScore + 0.4 * distanceScore

  extension (shoot: Decision.Shoot)
    def rate(state: MatchState): Double =
      val distance = shoot.striker.position.getDistance(shoot.goal)
      val teamId = if state.teams.head.players.contains(shoot.striker) then state.teams.head.id else state.teams.last.id
      if (
        distance > MatchConfig.highDistanceShoot || !Util.isPathClear(
          shoot.striker.position,
          shoot.goal,
          state,
          teamId
        ) || shoot.striker.decision == Decision.Initial
      ) then 0.0
      else if distance <= MatchConfig.lowDistanceShoot then 1.0
      else 0.20

  extension (moveToGoal: Decision.MoveToGoal)
    def rate(player: Player, state: MatchState): Double =
      val goalPos =
        if state.teams.head.players.contains(player)
        then Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
        else Position(0, UIConfig.fieldHeight / 2)
      val distanceToGoal = player.position.getDistance(goalPos)
      val directionClear = Util.isDirectionClear(player.position, moveToGoal.goalDirection, state)
      if player.decision == Decision.Initial then 0.0
      else if !directionClear then 0.0
      else if distanceToGoal < MatchConfig.lowDistanceShoot then 0.2
      else if distanceToGoal <= MatchConfig.highDistanceShoot then 0.5
      else 0.8

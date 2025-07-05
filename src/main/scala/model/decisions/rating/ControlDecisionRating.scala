package model.decisions.rating
import model.Match.*
import config.MatchConfig
import config.Util
import config.UIConfig
import dsl.SpaceSyntax.TeamsSyntax.*

object ControlDecisionRating:
  extension (run: Decision.Run)
    def rate(player: Player, state: Match): Double =
      if Util.isDirectionClear(player.position, run.direction, state)
      then 0.2
      else 0.0

  extension (pass: Decision.Pass)
    def rate(state: Match): Double =
      val from        = pass.from.position
      val to          = pass.to.position
      val team        = state.teams.teamOf(pass.from)
      val pathClear   = Util.isPathClear(from, to, state, team)
      val advancement = if state.teams.head.players.contains(pass.from) then to.x - from.x else from.x - to.x
      val distance    = from.getDistance(to)
      if !pathClear then 0.0
      else if distance < 30 && advancement > 20 then 1.0
      else if advancement > 50 && distance < 100 then 0.8
      else if advancement > 0 && distance < 100 then 0.5
      else if advancement > 0 then 0.2
      else 0.0

  extension (shoot: Decision.Shoot)
    def rate(state: Match): Double =
      val distance = shoot.striker.position.getDistance(shoot.goal)
      if (
        distance > MatchConfig.highDistanceToGoal || !Util.isPathClear(
          shoot.striker.position,
          shoot.goal,
          state,
          state.teams.teamOf(shoot.striker)
        )
      ) then 0.0
      else if distance <= MatchConfig.lowDistanceToGoal then 1.0
      else 0.2

  extension (moveToGoal: Decision.MoveToGoal)
    def rate(player: Player, state: Match): Double =
      val isTeamHead = state.teams.head.players.contains(player)
      val isOffensiveHalf =
        if isTeamHead
        then player.position.x > UIConfig.fieldWidth / 2
        else player.position.x < UIConfig.fieldWidth / 2
      if !isOffensiveHalf then 0.0
      else
        val directionClear = Util.isDirectionClear(player.position, moveToGoal.goalDirection, state)
        if !directionClear then 0.0
        else 0.7

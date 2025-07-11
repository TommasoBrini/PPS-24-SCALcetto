package dsl.creation

import config.MatchConfig.teamSize
import config.UIConfig.{fieldHeight, fieldWidth}
import model.Match.{Match, Score, Side}
import model.Match.Side.{East, West}
import dsl.creation.CreationSyntax.*
import dsl.creation.build.MatchBuilder
import dsl.creation.build.TeamBuilder
import model.Space.Direction
import dsl.MatchSyntax.*

import scala.util.Random

object SituationGenerator:
  def kickOff(score: Score, side: Side = West): Match =
    newMatch(score):
      if side == West then
        startingTeam(West)
        defendingTeam(East)
      else
        startingTeam(East)
        defendingTeam(West)
      ball at (fieldWidth / 2, fieldHeight / 2) move (Direction(0, 0), 0)

  private def startingTeam(side: Side)(using mb: MatchBuilder): TeamBuilder =
    team(side) withBall:
      val minX = side match
        case West => 1
        case East => fieldWidth / 2 + 1
      val maxX = side match
        case West => fieldWidth / 2 - 1
        case East => fieldWidth - 2

      (0 until teamSize).zipWithIndex.foreach: (i, idx) =>
        val x = Random.between(minX, maxX + 1)
        val y = Random.between(1, fieldHeight)

        val newPlayer = player(side.seed * 10 + idx) at (x, y)
        if idx == 0 then
          newPlayer at (fieldWidth / 2, fieldHeight / 2) ownsBall true

  private def defendingTeam(side: Side)(using mb: MatchBuilder): TeamBuilder =
    team(side):
      val minX = side match
        case West => 1
        case East => fieldWidth / 2 + 1
      val maxX = side match
        case West => fieldWidth / 2 - 1
        case East => fieldWidth - 2
      (0 until teamSize).zipWithIndex.foreach: (i, idx) =>
        val x = Random.between(minX, maxX + 1)
        val y = Random.between(1, fieldHeight)
        player(side.seed * 10 + idx) at (x, y)

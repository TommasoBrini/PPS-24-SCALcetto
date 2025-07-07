package dsl.creation

import config.MatchConfig.teamSize
import config.UIConfig.{fieldHeight, fieldWidth}
import model.Match.{Match, Side}
import model.Match.Side.{East, West}
import dsl.creation.CreationSyntax.*
import dsl.creation.build.MatchBuilder
import dsl.creation.build.TeamBuilder
import model.Space.Direction
import dsl.MatchSyntax.*

import scala.util.Random

object GenSituation:
  val kickOff: Match =
    newMatch {

      /* ---------- WEST team (ball carrier) ---------- */
      team(West) withBall { // makes mb: TeamBuilder implicit
        val minX = 1
        val maxX = fieldWidth / 2 - 1

        (0 until teamSize).zipWithIndex.foreach { (i, idx) =>
          val x = Random.between(minX, maxX + 1)
          val y = Random.between(1, fieldHeight)

          // West IDs are 10, 11, 12 …
          val pb = player(West.seed * 10 + idx).at(x, y)
          if idx == 0 then // first player starts as carrier
            pb ownsBall true
        }
      }

      /* ---------- EAST team (opponent) ------------- */
      team(East) {
        val minX = fieldWidth / 2 + 1
        val maxX = fieldWidth - 2

        (0 until teamSize).zipWithIndex.foreach { (i, idx) =>
          val x = Random.between(minX, maxX + 1)
          val y = Random.between(1, fieldHeight)
          // East IDs are 20, 21, 22 …
          player(East.seed * 10 + idx) at (x, y)
        }
      }

      /* --------------- the ball -------------------- */
      ball
        .at(fieldWidth / 2, fieldHeight / 2)
        .move(Direction(0, 0), 0)
    }

  // TODO def corner(attackingTeam: Side, state: Match): Match = ???

  // TODO def throwIn(attackingTeam: Side, state: Match): Match = ???

package dsl.game

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Ball, Player, Team}
import model.Space.Bounce.*
import TeamsSyntax.*

import scala.util.Random

class TestTeamsSyntax extends AnyFlatSpec with Matchers:

  "A (Team, Teams) " should " return its opponent if requested" in:
    val teamA: Team         = Team(1, List(), true)
    val teamB: Team         = Team(2, List())
    val teams: (Team, Team) = (teamA, teamB)
    (teams opponentOf teamB, teams opponentOf teamB.id) shouldBe (teamA, teamA)

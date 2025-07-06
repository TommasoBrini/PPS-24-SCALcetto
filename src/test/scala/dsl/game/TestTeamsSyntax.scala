package dsl.game

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Ball, Player, Team}
import model.Space.Bounce.*
import model.decisions.PlayerDecisionFactory.*
import TeamsSyntax.*

import scala.util.Random

class TestTeamsSyntax extends AnyFlatSpec with Matchers:

  "A (Team, Teams) " should " return its opponent if requested" in:
    val teamA: Team         = Team(List(), true)
    val teamB: Team         = Team(List())
    val teams: (Team, Team) = (teamA, teamB)
    teams opponentOf teamB shouldBe teamA

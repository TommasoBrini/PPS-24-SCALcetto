package dsl.game

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Ball, Player, Side, Team}
import model.Space.Bounce.*
import TeamsSyntax.*

import scala.util.Random
import dsl.creation.CreationSyntax.*
import model.Match.Side.{East, West}

class TestTeamsSyntax extends AnyFlatSpec with Matchers:

  "A (Team, Teams) " should " return its opponent if requested" in:
    val teamA: Team         = Team(List(), true)
    val teamB: Team         = Team(List())
    val teams: (Team, Team) = (teamA, teamB)
    teams opponentOf teamB shouldBe teamA

  "(Team, Teams).teamEast and West " should " be in the right side" in:
    val teamA: Team         = teamSolo(West).withBall.build()
    val teamB: Team         = teamSolo(East).build()
    val teams: (Team, Team) = (teamA, teamB)
    teams.teamEast.side shouldBe Side.East
    teams.teamWest.side shouldBe Side.West

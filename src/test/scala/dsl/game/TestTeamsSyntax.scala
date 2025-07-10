package dsl.game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Side, Team}
import TeamsSyntax.*
import dsl.creation.CreationSyntax.*
import dsl.creation.build.TeamBuilder
import model.Match.Side.{East, West}

class TestTeamsSyntax extends AnyFlatSpec with Matchers:

  "A (Team, Teams) " should " return its opponent if requested" in:
    val teamA: Team         = TeamBuilder(West).withBall.build()
    val teamB: Team         = TeamBuilder(East).build()
    val teams: (Team, Team) = (teamA, teamB)
    teams opponentOf teamB shouldBe teamA

  "(Team, Teams).teamEast and West " should " be in the right side" in:
    val teamA: Team         = TeamBuilder(West).withBall.build()
    val teamB: Team         = TeamBuilder(East).build()
    val teams: (Team, Team) = (teamA, teamB)
    teams.teamEast.side shouldBe Side.East
    teams.teamWest.side shouldBe Side.West

  "(Team, Teams).teamEast and West " should " throw IllegalArgumentExeption in badly written teams" in:
    val teamA: Team         = TeamBuilder(West).withBall.build()
    val teamB: Team         = TeamBuilder(West).build()
    val teams: (Team, Team) = (teamA, teamB)
    an[IllegalArgumentException] should be thrownBy teams.teamEast.side
    an[IllegalArgumentException] should be thrownBy teams.teamWest.side

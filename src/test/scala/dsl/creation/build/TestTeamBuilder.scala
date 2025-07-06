package dsl.creation.build

import model.Space.Position
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Side, Team}

class TestTeamBuilder extends AnyFlatSpec with Matchers:
  "TeamBuilder" should "build a West side team if specified" in:
    val newTeam: Team = TeamBuilder(Side.West).build()
    newTeam.side shouldBe Side.West

  "TeamBuilder" should "build a Team with no players if not specified" in:
    val newTeam: Team = TeamBuilder(Side.West).build()
    newTeam.players shouldBe List.empty

  "TeamBuilder" should "build a Team with no ball if not specified" in:
    val newTeam: Team = TeamBuilder(Side.West).build()
    newTeam.hasBall shouldBe false

  // TODO a test for withPlayer

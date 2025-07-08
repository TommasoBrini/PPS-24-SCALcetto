package dsl.creation.build

import dsl.creation.CreationSyntax.player
import model.Space.Position
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Side, Team}

class TestTeamBuilder extends AnyFlatSpec with Matchers:
  private def mk(id: Int) = PlayerBuilder(id).at(id, id).build()

  "TeamBuilder" should "collect players, side and hasBall flag" in {
    val team = TeamBuilder(Side.West)
      .withBall {
        player(10).at(1, 1)
        player(11).at(2, 2)
      }.build()

    team.side shouldBe Side.West
    team.players.map(_.id) should contain theSameElementsAs Seq(10, 11)
    team.hasBall shouldBe true
  }

  it should "allow multiple player() calls before build()" in {
    val team = TeamBuilder(Side.East) {
      player(20) at (3, 3)
      player(21) at (4, 4)
      player(22) at (5, 5)
    }.build()
    team.players.size shouldBe 3
  }

  it should "build a West side team if specified" in:
    val newTeam: Team = TeamBuilder(Side.West).build()
    newTeam.side shouldBe Side.West

  it should "build a Team with no players if not specified" in:
    val newTeam: Team = TeamBuilder(Side.West).build()
    newTeam.players shouldBe List.empty

  it should "build a Team with no ball if not specified" in:
    val newTeam: Team = TeamBuilder(Side.West).build()
    newTeam.hasBall shouldBe false

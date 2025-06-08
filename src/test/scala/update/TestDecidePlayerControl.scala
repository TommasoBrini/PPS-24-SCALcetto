package update

package update

import model.Model.*
import model.Model.Action.Move
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestDecidePlayerControl extends AnyFlatSpec with Matchers:

  "Player in team with ball" should "take decision" in:
    val player: Player = Player(1, Position(0, 0), PlayerStatus.teamControl, None, Movement(Direction.zero, 0))
    val newPlayer      = Decide.decideOfPlayerInTeamWithBall(player)
    newPlayer.nextAction.isDefined shouldBe true

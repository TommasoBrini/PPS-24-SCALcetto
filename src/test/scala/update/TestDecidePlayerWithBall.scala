package update

import model.Model.*
import model.Model.Action.Move
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestDecidePlayerWithBall extends AnyFlatSpec with Matchers:

  "Success rate" should "be a value between 0 and 1" in:
    val ballPlayer: Player = Player(1, Position(0, 0), PlayerStatus.ballControl, None, Movement(Direction.none, 0))
    Decide.passSuccessRate(ballPlayer) should (be >= 0.0 and be <= 1.0)
    Decide.moveForwardSuccessRate(ballPlayer) should (be >= 0.0 and be <= 1.0)
    Decide.shootSuccessRate(ballPlayer) should (be >= 0.0 and be <= 1.0)

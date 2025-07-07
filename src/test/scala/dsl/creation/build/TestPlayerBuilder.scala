package dsl.creation.build

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import model.Space.{Direction, Movement, Position}
import model.Match.*
import org.scalatest.matchers.should.Matchers

final class TestPlayerBuilder extends AnyFlatSpec with Matchers {

  "PlayerBuilder" should "apply every mutator and materialise an equivalent Player" in {
    val player = PlayerBuilder(42)
      .at(5, 6)
      .move(Direction(0, 1))(speed = 4)
      .ownsBall(true)
      .decidedTo(Decision.Confusion(5))
      .isGoingTo(Action.Stopped(4))
      .build()

    player.id shouldBe 42
    player.position shouldBe Position(5, 6)
    player.movement shouldBe Movement(Direction(0, 1), 4)
    player.ball.isDefined shouldBe true
    player.decision shouldBe Decision.Confusion(5)
    player.nextAction shouldBe Action.Stopped(4)
  }

  it should "produce a player **without** a ball when ownsBall(false) is used" in {
    PlayerBuilder(1).ownsBall(false).build().ball shouldBe None
  }
}

package dsl.creation.build

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Space.{Direction, Movement, Position}
import org.scalatest.matchers.should.Matchers

class BallBuilderSpec extends AnyFlatSpec with Matchers {

  "BallBuild " should " build a Ball with the chosen position and movement" in:
    val builder = BallBuilder()
      .at(10, 20)
      .move(Direction(1, -1), 3)

    val ball = builder.build()

    ball.position shouldBe Position(10, 20)
    ball.movement shouldBe Movement(Direction(1, -1), 3)

  "BallBuild defaults " should " be origin & still movement when nothing is set" in:
    BallBuilder().build() shouldBe model.Match.Ball(Position(0, 0), Movement.still)

}

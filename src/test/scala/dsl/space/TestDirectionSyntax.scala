package dsl.space

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Match.{Ball, Player, Team}
import model.Space.Bounce.*
import DirectionSyntax.*

import scala.util.Random

class TestDirectionSyntax extends AnyFlatSpec with Matchers:

  "A Direction" should "reflect correctly when bounced" in:
    val d = Direction(1.0, -1.0)
    (d getDirectionFrom ObliqueBounce, d getDirectionFrom HorizontalBounce, d getDirectionFrom VerticalBounce) shouldBe
      (Direction(-1.0, 1.0), Direction(-1.0, -1.0), Direction(1.0, 1.0))

  "Direction jitter" should " add the same noise when Random is seeded" in:
    Random.setSeed(42L)
    val direction = Direction(1.0, -3.0)
    val first     = direction.jitter

    Random.setSeed(42L)
    val second = direction.jitter

    assert(first == second)
    assert(math.abs(first.x - direction.x) <= 0.2)
    assert(math.abs(first.y - direction.y) <= 0.2)

package dsl

import model.Space.{Direction, Movement, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import dsl.SpaceSyntax.*
import model.Match.{Side, Team}
import Side.*
import model.Space.Bounce.*

import scala.util.Random

class TestSpaceSyntax extends AnyFlatSpec with Matchers:

  "isOutOfBound" should "return false for positions within bounds" in:
    val inside = Position(2, 2)
    inside isOutOfBound (10, 10) shouldBe false

  "isOutOfBound" should "return true for positions out of bounds" in:
    val outside = Position(-1, 11)
    outside.isOutOfBound(10, 10) shouldBe true

  "getBounce" should "return Both if x and y are out of bounds" in:
    val p = Position(-1, -1)
    p.getBounce(10, 10) shouldBe ObliqueBounce

  "getBounce" should "return Horizontal if only x is out of bounds" in:
    val p = Position(-1, 5)
    p.getBounce(10, 10) shouldBe HorizontalBounce

  "getBounce" should "return Vertical if only y is out of bounds" in:
    val p = Position(5, -1)
    p.getBounce(10, 10) shouldBe VerticalBounce

  "A Direction" should "reflect correctly when bounced" in:
    val d = Direction(1.0, -1.0)
    (d getDirectionFrom ObliqueBounce, d getDirectionFrom HorizontalBounce, d getDirectionFrom VerticalBounce) shouldBe
      (Direction(-1.0, 1.0), Direction(-1.0, -1.0), Direction(1.0, 1.0))

  "A Movement" should "bounce its direction correctly and keep speed" in:
    val m       = Movement(Direction(1.0, -1.0), 5)
    val bounced = m getMovementFrom ObliqueBounce
    bounced.direction shouldBe Direction(-1.0, 1.0)
    bounced.speed shouldBe 5

  "A Movement" should "be applied correctly to a position, returning the new position" in:
    val p      = Position(0, 0)
    val d      = Direction(1.0, 0.0)
    val m      = Movement(d, 3)
    val newPos = p + m
    newPos shouldBe Position(3, 0)

  "Direction jitter" should " add the same noise when Random is seeded" in:
    Random.setSeed(42L)
    val direction = Direction(1.0, -3.0)
    val first     = direction.jitter

    Random.setSeed(42L)
    val second = direction.jitter

    assert(first == second)
    assert(math.abs(first.x - direction.x) <= 0.2)
    assert(math.abs(first.y - direction.y) <= 0.2)

  "A Team " should " return the opponents if asked for it" in:
    val teamA: Team         = Team(Nil, West, true)
    val teamB: Team         = Team(Nil, East)
    val teams: (Team, Team) = (teamA, teamB)
    teams opponentOf teamB shouldBe teamA

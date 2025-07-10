package dsl.space

import model.Space.Position
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import model.Space.Bounce.*
import PositionSyntax.*
import config.UIConfig

class PositionSyntaxSpec extends AnyFlatSpec with Matchers:
  private val firstPost  = (UIConfig.fieldHeight - UIConfig.goalHeight) / 2
  private val secondPost = firstPost + UIConfig.goalHeight
  private val xWest      = 0                   // goal line at the west side
  private val xEast      = UIConfig.fieldWidth // goal line at the east side

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

  "goalWest" should "return true for any point on the east goal line" in:
    val insideEast = Seq(
      Position(xEast, firstPost),
      Position(xEast, (firstPost + secondPost) / 2),
      Position(xEast, secondPost)
    )
    insideEast.foreach:
      _.goalWest shouldBe true

  it should "return false for points outside the east goal rectangle" in:
    val outsideEast = Seq(
      Position(xEast, firstPost - 1),  // above cross-bar
      Position(xEast, secondPost + 1), // below goal
      Position(xEast - 10, firstPost)  // in the field
    )
    outsideEast.foreach:
      _.goalWest shouldBe false

  "goalEast" should "return true for any point on the west goal line" in:
    val insideWest = Seq(
      Position(xWest, firstPost),
      Position(xWest, (firstPost + secondPost) / 2),
      Position(xWest, secondPost)
    )

    insideWest.foreach:
      _.goalEast shouldBe true

  it should "return false for points outside the west goal rectangle" in:
    val outsideWest = Seq(
      Position(xWest, firstPost - 1),  // above
      Position(xWest, secondPost + 1), // below
      Position(xWest + 10, firstPost)  // in the field
    )
    outsideWest.foreach:
      _.goalEast shouldBe false

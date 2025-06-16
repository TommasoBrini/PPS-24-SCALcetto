package model

import scala.annotation.targetName

object Space:

  opaque type Position = (Int, Int)
  object Position:
    def apply(x: Int, y: Int): Position = (x, y)
  extension (p: Position)
    def x: Int = p._1
    def y: Int = p._2

  opaque type Direction = (Double, Double)
  object Direction:
    def apply(x: Int, y: Int): Direction       = (x, y)
    def apply(x: Double, y: Double): Direction = (x, y)
    def none: Direction                        = (0, 0)
  extension (d: Direction)
    def x: Double = d._1
    def y: Double = d._2

  extension (p: Position)
    def getDirection(to: Position): Direction =
      val dx = to.x.toDouble - p.x
      val dy = to.y.toDouble - p.y
      (dx / Math.hypot(dx, dy), dy / Math.hypot(dx, dy))

  case class Movement(direction: Direction, speed: Int)
  object Movement:
    def still: Movement = Movement(Direction.none, 0)

  extension (p: Position)
    @targetName("applyMovement")
    def +(m: Movement): Position =
      val dx = m.direction.x * m.speed
      val dy = m.direction.y * m.speed
      val x  = (p.x + dx).round.toInt
      val y  = (p.y + dy).round.toInt
      Position(x, y)

  enum Action:
    case Move(direction: Direction, speed: Int)
    case Hit(direction: Direction, speed: Int)
    case Take(ball: Ball)

  case class Player(
      id: Int,
      position: Position,
      movement: Movement,
      ball: Option[Ball] = None,
      nextAction: Option[Action] = None
  ):
    def hasBall: Boolean = ball.isDefined

  case class Team(id: Int, players: List[Player])

  case class Ball(position: Position, movement: Movement)

  case class MatchState(teams: List[Team], ball: Ball)

  enum Event:
    case StepEvent
    case DecideEvent
    case ActEvent
    case GoalEvent
    case RestartEvent

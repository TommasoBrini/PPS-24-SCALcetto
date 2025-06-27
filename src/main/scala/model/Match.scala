package model

import Space.*

object Match:

  export Space.*
  enum Action:
    case Initial
    case Stopped(remainingStep: Int)
    case Move(direction: Direction, speed: Int)
    case Hit(direction: Direction, speed: Int)
    case Take(ball: Ball)

  enum Decision:
    case Initial
    case Confusion(remainingStep: Int)
    case Pass(from: Player, to: Player)
    case Shoot(striker: Player, goal: Position)
    case Run(direction: Direction)
    case MoveToGoal(goalDirection: Direction)
    case Tackle(ball: Ball)
    case ReceivePass(ball: Ball)
    case Intercept(ball: Ball)
    case MoveToBall(directionToBall: Direction)
    case MoveRandom(direction: Direction, steps: Int)
    case Mark(defender: Player, target: Player)

  case class Player(
      id: Int,
      position: Position,
      movement: Movement = Movement.still,
      ball: Option[Ball] = None,
      nextAction: Action = Action.Initial,
      decision: Decision = Decision.Initial
  ):
    def hasBall: Boolean = ball.isDefined

  case class Team(id: Int, players: List[Player], hasBall: Boolean = false)

  case class Ball(position: Position, movement: Movement = Movement.still):
    def isHeadingToward(player: Player, tolerance: Double): Boolean =
      val toPlayer: Direction = position.getDirection(player.position)
      val actual: Direction   = movement.direction
      Math.abs(actual.x - toPlayer.x) + Math.abs(actual.y - toPlayer.y) < tolerance

  case class MatchState(teams: List[Team], ball: Ball)

  enum Event:
    case StepEvent
    case DecideEvent
    case ValidateEvent
    case ActEvent
    case BallOutEvent
    case GoalEvent
    case RestartEvent

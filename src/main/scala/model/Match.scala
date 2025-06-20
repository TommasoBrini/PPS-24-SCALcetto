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
    case MoveToGoal(attacker: Player, goal: Position)
    case MoveToBall(direction: Direction, speed: Int)
    case Tackle(ball: Ball)

  case class Player(
      id: Int,
      position: Position,
      movement: Movement,
      ball: Option[Ball] = None,
      nextAction: Action = Action.Initial,
      decision: Decision = Decision.Initial
  ):
    def hasBall: Boolean = ball.isDefined

  case class Team(id: Int, players: List[Player])

  case class Ball(position: Position, movement: Movement)

  case class MatchState(teams: List[Team], ball: Ball)

  enum Event:
    case StepEvent
    case DecideEvent
    case ValidateEvent
    case ActEvent
    case BallOutEvent
    case GoalEvent
    case RestartEvent

package model

import Space.*
import config.FieldConfig

//TODO decide a naming convention and use it
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
    // TODO this "toAction" should not be here in my opinion
    def toAction: Action = this match
      case Initial                      => Action.Initial
      case Confusion(step)              => Action.Stopped(step)
      case Tackle(ball)                 => Action.Take(ball)
      case Pass(from, to)               => Action.Hit(from.position.getDirection(to.position), FieldConfig.ballSpeed)
      case Shoot(striker, goal)         => Action.Hit(striker.position.getDirection(goal), FieldConfig.ballSpeed)
      case MoveToGoal(attacker, goal)   => Action.Move(attacker.position.getDirection(goal), FieldConfig.playerSpeed)
      case MoveToBall(direction, speed) => Action.Move(direction, speed)

  case class Player(
      id: Int,
      position: Position,
      movement: Movement,
      ball: Option[Ball] = None,
      // TODO i think Decision and Action should be Option
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

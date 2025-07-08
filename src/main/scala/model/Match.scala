package model

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
    case Run(direction: Direction, steps: Int)
    case Pass(from: Player, to: Player)
    case Shoot(striker: Player, goal: Position)
    case MoveToGoal(goalDirection: Direction)

    case Mark(defender: Player, target: Player, teamSide: Side)
    case Tackle(ball: Ball)
    case Intercept(ball: Ball)
    case MoveToBall(directionToBall: Direction)

    case MoveRandom(direction: Direction, steps: Int)
    case ReceivePass(ball: Ball)

  type ID = Int
  case class Player(
      id: ID,
      position: Position,
      movement: Movement = Movement.still,
      ball: Option[Ball] = None,
      decision: Decision = Decision.Initial,
      nextAction: Action = Action.Initial
  )

  enum Side:
    case West, East

  case class Team(players: List[Player], side: Side, hasBall: Boolean = false)

  object Team:
    import Side.West
    def apply(players: List[Player], hasBall: Boolean): Team = Team(players, West, hasBall)
    def apply(players: List[Player]): Team                   = Team(players, false)
    def apply(players: List[Player], side: Side): Team       = Team(players, side, false)

  case class Ball(position: Position, movement: Movement = Movement.still)

  case class Match(teams: (Team, Team), ball: Ball):
    def map(mapper: Match => Match): Match = mapper.apply(this)
    def mapIf(condition: Match => Boolean, mapper: Match => Match): Match =
      if condition.apply(this) then map(mapper) else this
    def players: List[Player] = teams._1.players ++ teams._2.players

import Space.*

package dsl.decisions.rating

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.Match.*
import model.Space.*
import dsl.decisions.rating.BallCarrierDecisionRating.*
import config.MatchConfig
import Side.*
import config.UIConfig

class RatingSpec extends AnyFlatSpec with Matchers:

  "Shoot rate" should "be 1.0 when the distance is near, no opponents and no initial decision" in:
    val ball = Ball(Position(10, 10))
    val goal = Position(10, MatchConfig.lowDistanceToGoal)
    val striker =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(0, 1)), Action.Initial)
    val shoot: Decision.Shoot = Decision.Shoot(striker, goal)
    val state                 = Match(teams = (Team(List(striker), East), Team(List(), West)), ball = ball)
    shoot.rate(state) shouldBe 1.0

  it should "be 0.20 when distance is high, no opponents and no initial decision" in:
    val ball = Ball(Position(10, 10))
    val goal = Position(10, MatchConfig.highDistanceToGoal)
    val striker =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(0, 1)), Action.Initial)
    val shoot: Decision.Shoot = Decision.Shoot(striker, goal)
    val state                 = Match(teams = (Team(List(striker), East), Team(List(), West)), ball = ball)
    shoot.rate(state) shouldBe 0.20

  it should "be 0.0 when an opponent is in trajectory" in:
    val ball = Ball(Position(10, 10))
    val striker =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(0, 1)), Action.Initial)
    val goal                  = Position(10, 14)
    val opponent              = Player(2, Position(10, 12))
    val shoot: Decision.Shoot = Decision.Shoot(striker, goal)
    val state                 = Match(teams = (Team(List(striker), West), Team(List(opponent), East)), ball)
    shoot.rate(state) shouldBe 0.0

  "Pass rate" should "be 0.0 if the path is not clear" in:
    val ball = Ball(Position(10, 10))
    val passer =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(1, 0)), Action.Initial)
    val receiver            = Player(2, Position(20, 10))
    val opponent            = Player(3, Position(15, 10))
    val pass: Decision.Pass = Decision.Pass(passer, receiver)
    val state               = Match(teams = (Team(List(passer, receiver), East), Team(List(opponent), West)), ball)
    pass.rate(state) shouldBe 0.0

  it should "be > 0 if the path is clear and receiver is advanced" in:
    val ball = Ball(Position(10, 10))
    val passer =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(1, 0)), Action.Initial)
    val receiver            = Player(2, Position(30, 10))
    val pass: Decision.Pass = Decision.Pass(passer, receiver)
    val state               = Match(teams = (Team(List(passer, receiver), West), Team(List(), East)), ball)
    pass.rate(state) should be > 0.0

  "Run rate" should "be 0.2 if direction is clear and forward" in:
    val ball = Ball(Position(10, 10))
    val runner =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(1, 0)), Action.Initial)
    val run: Decision.Run = Decision.Run(Direction(1, 0), MatchConfig.runSteps)
    val state             = Match(teams = (Team(List(runner), West), Team(List(), East)), ball)
    run.rate(runner, state) shouldBe 0.2

  it should "be 0.0 if direction is not clear" in:
    val ball = Ball(Position(10, 10))
    val runner =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(1, 0)), Action.Initial)
    val opponent          = Player(2, Position(15, 10))
    val run: Decision.Run = Decision.Run(Direction(1, 0), MatchConfig.runSteps)
    val state             = Match(teams = (Team(List(runner), East), Team(List(opponent), West)), ball)
    run.rate(runner, state) shouldBe 0.0

  "MoveToGoal rate" should "be 0.0 if direction is clear and away from goal" in:
    val ball = Ball(Position(10, 10))
    val player =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(1, 0)), Action.Initial)
    val move: Decision.MoveToGoal = Decision.MoveToGoal(Direction(1, 0))
    val state                     = Match(teams = (Team(List(player), West), Team(List(), East)), ball)
    move.rate(player, state) shouldBe 0.0

  it should "be 0.0 if direction is not clear" in:
    val ball = Ball(Position(10, 10))
    val player =
      Player(1, Position(10, 10), Movement.still, Some(ball), Decision.MoveToGoal(Direction(1, 0)), Action.Initial)
    val opponent                  = Player(2, Position(15, 10))
    val move: Decision.MoveToGoal = Decision.MoveToGoal(Direction(1, 0))
    val state                     = Match(teams = (Team(List(player), West), Team(List(opponent), East)), ball)
    move.rate(player, state) shouldBe 0.0

  it should "be 0.7 if close to goal" in:
    val ball = Ball(Position(UIConfig.fieldWidth - 2, UIConfig.fieldHeight / 2))
    val player = Player(
      1,
      Position(UIConfig.fieldWidth - 2, UIConfig.fieldHeight / 2),
      Movement.still,
      Some(ball),
      Decision.MoveToGoal(Direction(1, 0)),
      Action.Initial
    )
    val goalPos                   = Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
    val move: Decision.MoveToGoal = Decision.MoveToGoal(player.position.getDirection(goalPos))
    val state                     = Match(teams = (Team(List(player), West), Team(List(), East)), ball)
    move.rate(player, state) shouldBe 0.7

package model.decisions.rating

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.Match.*
import model.Space.*
import model.decisions.rating.ControlDecisionRating.*
import config.MatchConfig
import config.UIConfig

class ControlDecisionRatingSpec extends AnyFlatSpec with Matchers:

  "Shoot rate" should "be 1.0 when the distance is near, no opponents and no initial decision" in:
    val ball = Ball(Position(10, 10))
    val goal = Position(10, MatchConfig.lowDistanceShoot)
    val striker =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(0, 1)))
    val shoot: Decision.Shoot = Decision.Shoot(striker, goal)
    val state                 = MatchState(teams = List(Team(1, List(striker))), ball = ball)
    shoot.rate(state) shouldBe 1.0

  it should "be 0.20 when distance is high, no opponents and no initial decision" in:
    val ball = Ball(Position(10, 10))
    val goal = Position(10, MatchConfig.highDistanceShoot)
    val striker =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(0, 1)))
    val shoot: Decision.Shoot = Decision.Shoot(striker, goal)
    val state                 = MatchState(teams = List(Team(1, List(striker))), ball = ball)
    shoot.rate(state) shouldBe 0.20

  it should "be 0.0 when an opponent is in trajectory" in:
    val ball = Ball(Position(10, 10))
    val striker =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(0, 1)))
    val goal                  = Position(10, 14)
    val opponent              = Player(2, Position(10, 12))
    val shoot: Decision.Shoot = Decision.Shoot(striker, goal)
    val state                 = MatchState(teams = List(Team(1, List(striker)), Team(2, List(opponent))), ball)
    shoot.rate(state) shouldBe 0.0

  it should "be 0.0 when the striker has an initial decision" in:
    val ball    = Ball(Position(10, 10))
    val goal    = Position(10, MatchConfig.highDistanceShoot)
    val striker = Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.Initial)
    val shoot: Decision.Shoot = Decision.Shoot(striker, goal)
    val state                 = MatchState(teams = List(Team(1, List(striker))), ball = ball)
    shoot.rate(state) shouldBe 0.0

  "Pass rate" should "be 0.0 if the path is not clear" in:
    val ball = Ball(Position(10, 10))
    val passer =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(1, 0)))
    val receiver            = Player(2, Position(20, 10))
    val opponent            = Player(3, Position(15, 10))
    val pass: Decision.Pass = Decision.Pass(passer, receiver)
    val state               = MatchState(teams = List(Team(1, List(passer, receiver)), Team(2, List(opponent))), ball)
    pass.rate(state) shouldBe 0.0

  it should "be > 0 if the path is clear and receiver is advanced" in:
    val ball = Ball(Position(10, 10))
    val passer =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(1, 0)))
    val receiver            = Player(2, Position(30, 10))
    val pass: Decision.Pass = Decision.Pass(passer, receiver)
    val state               = MatchState(teams = List(Team(1, List(passer, receiver))), ball)
    pass.rate(state) should be > 0.0

  "Run rate" should "be 0.4 if direction is clear and not initial" in:
    val ball = Ball(Position(10, 10))
    val runner =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(1, 0)))
    val run: Decision.Run = Decision.Run(Direction(1, 0))
    val state             = MatchState(teams = List(Team(1, List(runner))), ball)
    run.rate(runner, state) shouldBe 0.4

  it should "be 0.0 if direction is not clear" in:
    val ball = Ball(Position(10, 10))
    val runner =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(1, 0)))
    val opponent          = Player(2, Position(15, 10))
    val run: Decision.Run = Decision.Run(Direction(1, 0))
    val state             = MatchState(teams = List(Team(1, List(runner)), Team(2, List(opponent))), ball)
    run.rate(runner, state) shouldBe 0.0

  "MoveToGoal rate" should "be 0.8 if direction is clear and away from goal" in:
    val ball = Ball(Position(10, 10))
    val player =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(1, 0)))
    val move: Decision.MoveToGoal = Decision.MoveToGoal(Direction(1, 0))
    val state                     = MatchState(teams = List(Team(1, List(player))), ball)
    move.rate(player, state) shouldBe 0.8

  it should "be 0.0 if direction is not clear" in:
    val ball = Ball(Position(10, 10))
    val player =
      Player(1, Position(10, 10), Movement.still, Some(ball), Action.Initial, Decision.MoveToGoal(Direction(1, 0)))
    val opponent                  = Player(2, Position(15, 10))
    val move: Decision.MoveToGoal = Decision.MoveToGoal(Direction(1, 0))
    val state                     = MatchState(teams = List(Team(1, List(player)), Team(2, List(opponent))), ball)
    move.rate(player, state) shouldBe 0.0

  it should "be 0.2 if close to goal" in:
    val ball = Ball(Position(UIConfig.fieldWidth - 2, UIConfig.fieldHeight / 2))
    val player = Player(
      1,
      Position(UIConfig.fieldWidth - 2, UIConfig.fieldHeight / 2),
      Movement.still,
      Some(ball),
      Action.Initial,
      Decision.MoveToGoal(Direction(1, 0))
    )
    val goalPos                   = Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
    val move: Decision.MoveToGoal = Decision.MoveToGoal(player.position.getDirection(goalPos))
    val state                     = MatchState(teams = List(Team(1, List(player))), ball)
    move.rate(player, state) shouldBe 0.2

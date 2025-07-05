package model.decisions

import model.Match.*
import model.Match.Decision.*
import model.Space.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.decisions.CommonPlayerDecisions.*
import config.UIConfig
import model.decisions.PlayerRoleFactory.*
import config.MatchConfig

class CommonPlayerDecisionsSpec extends AnyFlatSpec with Matchers:

  "CommonPlayerDecisions.decideRun" should "create Run decision with correct direction" in:
    val player    = Player(1, Position(5, 5), Movement.still)
    val direction = Direction(1, 0)
    val decision  = player.decideRun(direction, MatchConfig.runSteps)

    decision shouldBe a[Run]
    decision.asInstanceOf[Run].direction shouldBe direction

  it should "work with different directions" in:
    val player = Player(1, Position(5, 5), Movement.still)
    val directions = List(
      Direction(1, 1),
      Direction(-1, -1),
      Direction(0, 1),
      Direction(1, 0),
      Direction(-1, 0),
      Direction(0, -1)
    )

    directions.foreach { direction =>
      val decision = player.decideRun(direction, MatchConfig.runSteps)
      decision shouldBe a[Run]
      decision.asInstanceOf[Run].direction shouldBe direction
    }

  "CommonPlayerDecisions.possibleRuns" should "return list of Run decisions" in:
    val player = Player(1, Position(5, 5), Movement.still)
    val team1  = Team(1, List(player), hasBall = true)
    val team2  = Team(2, List(), hasBall = false)
    val state  = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleRuns = player.possibleRunDirections(state)

    possibleRuns should not be empty
    possibleRuns.foreach { decision =>
      decision shouldBe a[Run]
    }

  it should "exclude stationary movement (0,0)" in:
    val player = Player(1, Position(5, 5), Movement.still)
    val team1  = Team(1, List(player), hasBall = true)
    val team2  = Team(2, List(), hasBall = false)
    val state  = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleRuns = player.possibleRunDirections(state)

    possibleRuns should not contain (Run(Direction(0, 0), MatchConfig.runSteps))

  it should "include all valid movement directions" in:
    val player = Player(1, Position(5, 5), Movement.still)
    val team1  = Team(1, List(player), hasBall = true)
    val team2  = Team(2, List(), hasBall = false)
    val state  = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleRuns = player.possibleRunDirections(state)

    possibleRuns should have size 4

  "CommonPlayerDecisions.decideConfusion" should "create Confusion decision with correct steps" in:
    val player   = Player(1, Position(5, 5), Movement.still)
    val steps    = 3
    val decision = player.decideConfusion(steps)

    decision shouldBe a[Confusion]
    decision.asInstanceOf[Confusion].remainingStep shouldBe steps

  it should "work with zero steps" in:
    val player   = Player(1, Position(5, 5), Movement.still)
    val decision = player.decideConfusion(0)

    decision shouldBe a[Confusion]
    decision.asInstanceOf[Confusion].remainingStep shouldBe 0

  "CommonPlayerDecisions.decideMoveToBall" should "create MoveToBall decision with correct direction" in:
    val player    = Player(1, Position(5, 5), Movement.still)
    val direction = Direction(1, 1)
    val decision  = player.decideMoveToBall(direction)

    decision shouldBe a[MoveToBall]
    decision.asInstanceOf[MoveToBall].directionToBall shouldBe direction

  "CanDecideToPass.decidePass" should "create Pass decision with correct players" in:
    val passer   = Player(1, Position(5, 5), Movement.still).asAttackingPlayer
    val receiver = Player(2, Position(6, 6), Movement.still)
    val decision = passer.decidePass(receiver)

    decision shouldBe a[Pass]
    decision.asInstanceOf[Pass].from shouldBe passer
    decision.asInstanceOf[Pass].to shouldBe receiver

  "CanDecideToPass.possiblePasses" should "return list of Pass decisions for teammates" in:
    val passer    = Player(1, Position(5, 5), Movement.still).asAttackingPlayer
    val teammate1 = Player(2, Position(6, 6), Movement.still)
    val teammate2 = Player(3, Position(7, 7), Movement.still)
    val opponent  = Player(4, Position(10, 10), Movement.still)
    val team1     = Team(1, List(passer, teammate1, teammate2), hasBall = true)
    val team2     = Team(2, List(opponent), hasBall = false)
    val state     = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possiblePasses = passer.possiblePasses(state)

    possiblePasses should have size 2
    possiblePasses.foreach { decision =>
      decision shouldBe a[Pass]
      decision.asInstanceOf[Pass].from shouldBe passer
      decision.asInstanceOf[Pass].to should (be(teammate1) or be(teammate2))
    }

  it should "not include passes to opponents" in:
    val passer   = Player(1, Position(5, 5), Movement.still).asAttackingPlayer
    val teammate = Player(2, Position(6, 6), Movement.still)
    val opponent = Player(3, Position(10, 10), Movement.still)
    val team1    = Team(1, List(passer, teammate), hasBall = true)
    val team2    = Team(2, List(opponent), hasBall = false)
    val state    = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possiblePasses = passer.possiblePasses(state)

    possiblePasses should have size 1
    possiblePasses.head.asInstanceOf[Pass].to shouldBe teammate

  it should "not include passes to self" in:
    val passer = Player(1, Position(5, 5), Movement.still).asAttackingPlayer
    val team1  = Team(1, List(passer), hasBall = true)
    val team2  = Team(2, List(), hasBall = false)
    val state  = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possiblePasses = passer.possiblePasses(state)

    possiblePasses should be(empty)

  "CanDecideToShoot.decideShoot" should "create Shoot decision with correct parameters" in:
    val striker  = Player(1, Position(5, 5), Movement.still).asAttackingPlayer
    val goal     = Position(10, 5)
    val decision = striker.decideShoot(goal)

    decision shouldBe a[Shoot]
    decision.asInstanceOf[Shoot].striker shouldBe striker
    decision.asInstanceOf[Shoot].goal shouldBe goal

  "CanDecideToShoot.possibleShots" should "return list of Shoot decisions for all goal positions" in:
    val striker = Player(1, Position(5, 5), Movement.still).asAttackingPlayer
    val team1   = Team(1, List(striker), hasBall = true)
    val team2   = Team(2, List(), hasBall = false)
    val state   = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleShots = striker.possibleShots(state)

    possibleShots should have size 3
    possibleShots.foreach { decision =>
      decision shouldBe a[Shoot]
      decision.asInstanceOf[Shoot].striker shouldBe striker
    }

  it should "include correct goal positions for team 1" in:
    val striker = Player(1, Position(5, 5), Movement.still).asAttackingPlayer
    val team1   = Team(1, List(striker), hasBall = true)
    val team2   = Team(2, List(), hasBall = false)
    val state   = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleShots = striker.possibleShots(state)

    val expectedGoals = List(
      Position(UIConfig.goalEastX, UIConfig.firstPoleY),
      Position(UIConfig.goalEastX, UIConfig.midGoalY),
      Position(UIConfig.goalEastX, UIConfig.secondPoleY)
    )

    possibleShots.map(_.asInstanceOf[Shoot].goal) should contain theSameElementsAs expectedGoals

  "CanDecideToMoveToGoal.decideMoveToGoal" should "create MoveToGoal decision with correct direction" in:
    val player    = Player(1, Position(5, 5), Movement.still).asAttackingPlayer
    val direction = Direction(1, 0)
    val decision  = player.decideMoveToGoal(direction)

    decision shouldBe a[MoveToGoal]
    decision.asInstanceOf[MoveToGoal].goalDirection shouldBe direction

  it should "return MoveToGoal decision for non-Initial decision" in:
    val player = Player(
      1,
      Position(5, 5),
      Movement.still,
      decision = Run(Direction(1, 0), MatchConfig.runSteps)
    ).asAttackingPlayer
    val team1 = Team(1, List(player), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleMoves = player.possibleMovesToGoal(state)

    possibleMoves should have size 1
    possibleMoves.head shouldBe a[MoveToGoal]

  it should "calculate correct direction to goal for team 1" in:
    val player = Player(
      1,
      Position(5, 5),
      Movement.still,
      decision = Run(Direction(1, 0), MatchConfig.runSteps)
    ).asAttackingPlayer
    val team1 = Team(1, List(player), hasBall = true)
    val team2 = Team(2, List(), hasBall = false)
    val state = MatchState((team1, team2), Ball(Position(0, 0), Movement.still))

    val possibleMoves = player.possibleMovesToGoal(state)

    val moveToGoal        = possibleMoves.head.asInstanceOf[MoveToGoal]
    val expectedGoal      = Position(UIConfig.fieldWidth, UIConfig.fieldHeight / 2)
    val expectedDirection = player.position.getDirection(expectedGoal)

    moveToGoal.goalDirection shouldBe expectedDirection

  "CanDecideToMark.decideMark" should "create Mark decision with correct players" in:
    val marker   = Player(1, Position(5, 5), Movement.still).asDefendingPlayer
    val target   = Player(2, Position(6, 6), Movement.still)
    val decision = marker.decideMark(target, 1)

    decision shouldBe a[Mark]
    decision.asInstanceOf[Mark].defender shouldBe marker
    decision.asInstanceOf[Mark].target shouldBe target

  "CanDecideToTackle.decideTackle" should "create Tackle decision with correct ball" in:
    val tackler  = Player(1, Position(5, 5), Movement.still).asDefendingPlayer
    val ball     = Ball(Position(6, 6), Movement.still)
    val decision = tackler.decideTackle(ball)

    decision shouldBe a[Tackle]
    decision.asInstanceOf[Tackle].ball shouldBe ball

  "CanDecideToIntercept.decideIntercept" should "create Intercept decision with correct ball" in:
    val interceptor = Player(1, Position(5, 5), Movement.still).asDefendingPlayer
    val ball        = Ball(Position(6, 6), Movement.still)
    val decision    = interceptor.decideIntercept(ball)

    decision shouldBe a[Intercept]
    decision.asInstanceOf[Intercept].ball shouldBe ball

  "CanDecideToMoveRandom.decideMoveRandom" should "create MoveRandom decision with correct parameters" in:
    val player    = Player(1, Position(5, 5), Movement.still).asTeammatePlayer
    val direction = Direction(1, 1)
    val steps     = 3
    val decision  = player.decideMoveRandom(direction, steps)

    decision shouldBe a[MoveRandom]
    decision.asInstanceOf[MoveRandom].direction shouldBe direction
    decision.asInstanceOf[MoveRandom].steps shouldBe steps

  "CanDecideToReceivePass.decideReceivePass" should "create ReceivePass decision with correct ball" in:
    val receiver = Player(1, Position(5, 5), Movement.still).asTeammatePlayer
    val ball     = Ball(Position(6, 6), Movement.still)
    val decision = receiver.decideReceivePass(ball)

    decision shouldBe a[ReceivePass]
    decision.asInstanceOf[ReceivePass].ball shouldBe ball

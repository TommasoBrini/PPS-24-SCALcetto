package update.decide

import init.GameInitializer
import model.Match.*
import model.player.Player
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import update.decide.behaviours.{ControlPlayerBehavior, OpponentBehavior, TeammateBehavior}

class TestDecisionStrategy extends AnyFlatSpec with Matchers {
  "BallPlayerStrategy" should "return an Option of Action" in:
    val state              = GameInitializer.initialSimulationState()
    val ballPlayer: Player = state.teams.flatMap(_.players).find(_.hasBall).get
    ControlPlayerBehavior.decide(ballPlayer, state) shouldBe a[Decision]

  "TeamPossessionStrategy" should "return an Option of Move" in:
    val state          = GameInitializer.initialSimulationState()
    val player: Player = state.teams.flatMap(_.players).head.asTeammate
    TeammateBehavior.decide(player, state) shouldBe a[Decision]

  "NoControlStrategy" should "return an Option of Move" in:
    val state          = GameInitializer.initialSimulationState()
    val player: Player = state.teams.flatMap(_.players).head.asOpponent
    OpponentBehavior(None).decide(player, state) shouldBe a[Decision.MoveToBall]
}

package update.decide

import config.FieldConfig
import init.GameInitializer
import model.Match.*
import model.Match.Action.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestDecisionStrategy extends AnyFlatSpec with Matchers {
  "BallPlayerStrategy" should "return an Option of Action" in:
    val state              = GameInitializer.initialSimulationState()
    val ballPlayer: Player = state.teams.flatMap(_.players).find(_.hasBall).get
    BallPlayerStrategy.decide(ballPlayer, state).nextAction shouldBe a[Option[Action]]

  "TeamPossessionStrategy" should "return an Option of Move" in:
    val state          = GameInitializer.initialSimulationState()
    val player: Player = state.teams.flatMap(_.players).head
    TeamPossesionStrategy.decide(player, state).nextAction.get shouldBe a[Action.Move]

  "NoControlStrategy" should "return an Option of Move" in:
    val state          = GameInitializer.initialSimulationState()
    val player: Player = state.teams.flatMap(_.players).head
    NoControlStrategy.decide(player, state).nextAction.get shouldBe a[Action.Move]
}

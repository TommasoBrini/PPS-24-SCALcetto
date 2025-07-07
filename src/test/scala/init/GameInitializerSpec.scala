//package init
//
//import config.UIConfig.*
//import config.MatchConfig.*
//import model.Match.Match
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//
//import scala.Tuple
//import model.Match.Team
//
//class GameInitializerSpec extends AnyFlatSpec with Matchers:
//
//  val initialState: Match = GameInitializer.initialSimulationState()
//
//  "Game Initializer" should "create initial simulation state with two teams and a ball" in:
//    initialState.ball should not be null
//
//  it should "assign players to teams correctly" in:
//    val teamA = initialState.teams.head
//    val teamB = initialState.teams(1)
//    teamA.players should have size teamSize
//    teamB.players should have size teamSize
//
//  it should "assign the ball to one player in team B" in:
//    initialState.teams.head.players.exists(_.ball.isDefined) shouldEqual false
//    initialState.teams(1).players.exists(_.ball.isDefined) shouldEqual true
//
//  it should "place the ball at the center of the field" in:
//    initialState.ball.position.x shouldEqual (fieldWidth / 2)
//    initialState.ball.position.y shouldEqual (fieldHeight / 2)
//
//  // TODO fix this test
////  it should "place players within the field boundaries" in:
////    initialState.teams.teamA.players.forall {  player =>
////      (player.position.x should be >= 1 &&
////        player.position.x should be <= fieldWidth - 2 &&
////        player.position.y should be >= 1 &&
////        player.position.y should be <= fieldHeight - 2)
////    }
//
//  it should "place players on the correct side of the field" in:
//    val teamA = initialState.teams.head
//    val teamB = initialState.teams(1)
//
//    teamA.players.foreach { player =>
//      player.position.x should be <= fieldWidth / 2
//    }
//    teamB.players.foreach { player =>
//      player.position.x should be >= fieldWidth / 2
//    }

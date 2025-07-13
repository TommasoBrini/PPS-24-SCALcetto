package dsl.action

import model.Match.*
import Decision.*
import Action.*
import dsl.MatchSyntax.*
import dsl.SpaceSyntax.*
import config.MatchConfig
import update.Update.Event
import update.Update.Event.{BallOut, GoalEast, GoalWest}

object ActionProcessor:
  extension (state: Match)
    def existsSuccessfulTackle: Boolean = state.players.exists {
      case Player(_, _, _, _, Tackle(_), Take(_)) => true
      case _                                      => false
    }

    def isPossessionChanging: Boolean = state.players.exists {
      case Player(_, _, _, _, _, Take(_)) => true
      case _                              => false
    }

    def tackleBallCarrier(): Match =
      state.copy(teams =
        state.teams.map((t: Team) =>
          t.copy(players = t.players.map({
            case p if p.hasBall => p.copy(ball = None, nextAction = Stopped(MatchConfig.stoppedAfterTackle))
            case p              => p
          }))
        )
      )

    def updateBallPossession(): Match =
      state.copy(teams = state.teams.map(_.updateBallPossession()))

    def updateMovements(): Match =
      val carrier = state.players.find(_.hasBall)
      val teams   = state.teams.map(_.processActions())
      val ball    = state.ball.updateMovement(carrier)
      state.copy(teams = teams, ball = ball)

    def moveEntities(): Match = Match(state.teams.map(_.move()), state.ball.move(), state.score)

    def detectEvent(): Option[Event] =
      if state.ball.position.goalWest then Some(GoalWest)
      else if state.ball.position.goalEast then Some(GoalEast)
      else if state.isBallOut then Some(BallOut)
      else None

  extension (team: Team)
    def updateBallPossession(): Team =
      val updatedPlayers = team.players.map({
        case p @ Player(_, _, _, _, _, Take(ball)) => p.copy(ball = Some(ball))
        case p                                     => p
      })
      team.copy(players = updatedPlayers, hasBall = updatedPlayers.exists(_.hasBall))
    def processActions(): Team = team.copy(players = team.players.map(_.processAction()))
    def move(): Team           = team.copy(players = team.players.map(_.move()))

  extension (player: Player)
    def updateBallPossession(): Player =
      player.nextAction match
        case Take(ball) => player.copy(ball = Some(ball))
        case _          => player
    def processAction(): Player = player.nextAction match
      case Hit(_, _) =>
        player.copy(movement = Movement.still, ball = None, nextAction = Stopped(MatchConfig.stoppedAfterHit))
      case Move(direction, speed) => player.copy(movement = Movement(direction, speed))
      case Stopped(duration)      => player.copy(movement = Movement.still)
      case Take(ball)             => player.copy(movement = Movement.still)
      case _                      => player
    def move(): Player =
      val newPosition = (player.position + player.movement).clampToField
      player.copy(position = newPosition)

  import config.UIConfig
  extension (ball: Ball)
    def updateMovement(carrier: Option[Player]): Ball = carrier match
      case Some(Player(_, _, _, _, _, Hit(direction, speed))) => ball.copy(movement = Movement(direction, speed))
      case Some(Player(_, position, _, _, _, Move(direction, speed))) =>
        val movement    = Movement(direction, speed)
        val newPosition = position + (movement * (UIConfig.ballSize / 2))
        ball.copy(position = newPosition.clampToField, movement = movement)
      case Some(Player(_, position, movement, _, _, Take(ball))) =>
        ball.copy(position = position + (movement * (UIConfig.ballSize / 2)), movement = movement)
      case _ => ball
    def move(): Ball = ball.copy(position = ball.position + ball.movement)

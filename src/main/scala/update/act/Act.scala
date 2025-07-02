package update.act

import config.{MatchConfig, UIConfig}
import model.Space.*
import model.Match.*
import model.Match.Action.*
import model.Match.Decision.{Intercept, Tackle}
import dsl.SpaceSyntax.*

import scala.language.postfixOps

object Act:
  extension (state: MatchState)
    def act(): MatchState =
      val changePossession = state.players.exists {
        case Player(_, _, _, _, Tackle(_) | Intercept(_), Take(_)) => true
        case _                                                     => false
      }
      if changePossession
      then changeBallPossession().updateMovements().moveEntities()
      else updateMovements().moveEntities()

    def changeBallPossession(): MatchState =
      val updatedTeams = state.teams.map(t => t.copy(hasBall = !t.hasBall))
      state.copy(teams = updatedTeams)

    def updateMovements(): MatchState =
      val carrier = state.players.find(_.hasBall)
      MatchState(state.teams.map(_.updateMovements()), state.ball.updateMovement(carrier))

    def moveEntities(): MatchState = MatchState(state.teams.map(_.move()), state.ball.move())

    def isGoal: Boolean =
      state.ball.position.isInsideGoal

    def isBallOut: Boolean =
      state.ball.position.isOutOfBound(UIConfig.fieldWidth, UIConfig.fieldHeight)

  extension (team: Team)
    def move(): Team            = team.copy(players = team.players.map(_.move()))
    def updateMovements(): Team = team.copy(players = team.players.map(_.updateMovement()))

  extension (player: Player)
    def move(): Player = player.copy(position = player.position + player.movement)
    def updateMovement(): Player = player.action match
      case Hit(_, _) =>
        player.copy(movement = Movement.still, ball = None, action = Stopped(MatchConfig.stoppedAfterHit))
      case Move(direction, speed) => player.copy(movement = Movement(direction, speed))
      case Stopped(duration)      => player.copy(movement = Movement.still)
      case Take(ball)             => player.copy(movement = Movement.still)
      case _                      => player

  extension (ball: Ball)
    def move(): Ball = ball.copy(position = ball.position + ball.movement)
    def updateMovement(carrier: Option[Player]): Ball = carrier match
      case Some(Player(_, _, _, _, _, Hit(direction, speed)))  => ball.copy(movement = Movement(direction, speed))
      case Some(Player(_, _, _, _, _, Move(direction, speed))) => ball.copy(movement = Movement(direction, speed))
      case Some(Player(_, position, movement, _, _, Take(ball))) =>
        ball.copy(position = position + (movement * (UIConfig.ballSize / 2)), movement = movement)
      case _ => ball

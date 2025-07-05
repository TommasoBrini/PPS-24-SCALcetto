package update.act

import config.{MatchConfig, UIConfig}
import model.Space.*
import model.Match.*
import model.Match.Action.*
import model.Match.Decision.{Intercept, Tackle}
import dsl.SpaceSyntax.*
import monads.States.*
import update.Update.Event
import update.Update.Event.*

import scala.language.postfixOps

object Act:

  def actStep: State[Match, Option[Event]] =
    State(s => {
      val updated = s.act()
      if isGoal(updated) then (updated, Some(Goal))
      else if isBallOut(updated) then (updated, Some(BallOut))
      else (updated, None)
    })

  extension (state: Match)
    def act(): Match =
      state
        .mapIf(existsSuccessfulTackle, tackleBallCarrier)
        .mapIf(isPossessionChanging, updateBallPossession)
        .map(updateMovements)
        .map(moveEntities)

    def isGoal: Boolean =
      state.ball.position.isInsideGoal

    def isBallOut: Boolean =
      state.ball.position.isOutOfBound(UIConfig.fieldWidth, UIConfig.fieldHeight)

  def existsSuccessfulTackle(state: Match): Boolean = state.players.exists {
    case Player(_, _, _, _, Tackle(_), Take(_)) => true
    case _                                      => false
  }

  def isPossessionChanging(state: Match): Boolean = state.players.exists {
    case Player(_, _, _, _, _, Take(_)) => true
    case _                              => false
  }

  def tackleBallCarrier(state: Match): Match =
    state.copy(teams =
      state.teams.map(t =>
        t.copy(players = t.players.map({
          case p if p.hasBall => p.copy(ball = None, action = Stopped(MatchConfig.stoppedAfterTackle))
          case p              => p
        }))
      )
    )

  def updateBallPossession(state: Match): Match =
    state.copy(teams = state.teams.map(_.updateBallPossession()))

  def updateMovements(state: Match): Match =
    val carrier = state.players.find(_.hasBall)
    val teams   = state.teams.map(_.updateMovements())
    val ball    = state.ball.updateMovement(carrier)
    Match(teams, ball)

  def moveEntities(state: Match): Match = Match(state.teams.map(_.move()), state.ball.move())

  extension (team: Team)
    def updateBallPossession(): Team =
      val updatedPlayers = team.players.map({
        case p @ Player(_, _, _, _, _, Take(ball)) => p.copy(ball = Some(ball))
        case p                                     => p
      })
      team.copy(players = updatedPlayers, hasBall = updatedPlayers.exists(_.hasBall))
    def updateMovements(): Team = team.copy(players = team.players.map(_.updateMovement()))
    def move(): Team            = team.copy(players = team.players.map(_.move()))

  extension (player: Player)
    def updateMovement(): Player = player.action match
      case Hit(_, _) =>
        player.copy(movement = Movement.still, ball = None, action = Stopped(MatchConfig.stoppedAfterHit))
      case Move(direction, speed) => player.copy(movement = Movement(direction, speed))
      case Stopped(duration)      => player.copy(movement = Movement.still)
      case Take(ball)             => player.copy(movement = Movement.still)
      case _                      => player
    def move(): Player = player.copy(position = player.position + player.movement)

  extension (ball: Ball)
    def updateMovement(carrier: Option[Player]): Ball = carrier match
      case Some(Player(_, _, _, _, _, Hit(direction, speed))) => ball.copy(movement = Movement(direction, speed))
      case Some(Player(_, position, _, _, _, Move(direction, speed))) =>
        val movement = Movement(direction, speed)
        ball.copy(position = position + (movement * (UIConfig.ballSize / 2)), movement = movement)
      case Some(Player(_, position, movement, _, _, Take(ball))) =>
        ball.copy(position = position + (movement * (UIConfig.ballSize / 2)), movement = movement)
      case _ => ball
    def move(): Ball = ball.copy(position = ball.position + ball.movement)

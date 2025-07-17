package dsl.action

import model.Match.*
import Decision.*
import Action.*
import dsl.MatchSyntax.*
import dsl.SpaceSyntax.*
import config.{MatchConfig, UIConfig}
import update.Update.Event
import update.Update.Event.{BallOut, GoalEast, GoalWest}

object ActionProcessor:
  extension (state: MatchState)
    /** Checks whether a player has successfully tackled and is taking the ball.
      *
      * @return
      *   true if any player completed a tackle and is taking the ball
      */
    def existsSuccessfulTackle: Boolean = state.players.exists {
      case Player(_, _, _, _, Tackle(_), Take(_)) => true
      case _                                      => false
    }

    /** Determines whether a change of possession is occurring due to a Take action.
      *
      * @return
      *   true if any player has a Take action
      */
    def isPossessionChanging: Boolean = state.players.exists {
      case Player(_, _, _, _, _, Take(_)) => true
      case _                              => false
    }

    /** Applies the effect of a successful tackle, removing the ball from the carrier and setting the player's next
      * action to a stopped state.
      *
      * @return
      *   the match state with ball carrier tackled
      */
    def tackleBallCarrier(): MatchState =
      state.copy(teams =
        state.teams.map((t: Team) =>
          t.copy(players = t.players.map({
            case p if p.hasBall => p.copy(ball = None, nextAction = Stopped(MatchConfig.stoppedAfterTackle))
            case p              => p
          }))
        )
      )

    /** Updates team-level ball possession by evaluating each player’s Take action. Players with Take(ball) gain the
      * ball, and team possession is updated accordingly.
      *
      * @return
      *   the match state with new player and team possession
      */
    def updateBallPossession(): MatchState =
      state.copy(teams = state.teams.map(_.updateBallPossession()))

    /** Applies movement updates for all teams and the ball. This processes player actions into movement, and the ball
      * follows the active carrier. There is an exception for Hit action that also removes ball from player and updates
      * his nextAction
      *
      * @return
      *   the match state with new movements
      */
    def updateMovements(): MatchState =
      val carrier = state.players.find(_.hasBall)
      val teams   = state.teams.map(_.processActions())
      val ball    = state.ball.updateMovement(carrier)
      state.copy(teams = teams, ball = ball)

    /** Moves all entities (teams and ball) by applying their respective movement.
      *
      * @return
      *   the state with new positions
      */
    def moveEntities(): MatchState = state.copy(teams = state.teams.map(_.move()), ball = state.ball.move())

    /** Detects if a major game event has occurred based on the ball’s current position.
      *
      * @return
      *   Some(Event) if goal or ball out is detected, otherwise None
      */
    def detectEvent(): Option[Event] =
      if state.ball.position.goalWest then Some(GoalWest)
      else if state.ball.position.goalEast then Some(GoalEast)
      else if state.isBallOut then Some(BallOut)
      else None

  extension (team: Team)
    /** Updates player possession within a team based on Take actions, and flags if the team has possession.
      *
      * @return
      *   the updated Team
      */
    def updateBallPossession(): Team =
      val updatedPlayers = team.players.map({
        case p @ Player(_, _, _, _, _, Take(ball)) => p.copy(ball = Some(ball))
        case p                                     => p
      })
      team.copy(players = updatedPlayers, hasBall = updatedPlayers.exists(_.hasBall))

    /** Applies each player's nextAction into an actual movement or stopped state.
      *
      * @return
      *   the team with processed actions
      */
    def processActions(): Team = team.copy(players = team.players.map(_.processAction()))

    /** Moves each player in the team based on their current movement.
      *
      * @return
      *   the team with moved players
      */
    def move(): Team = team.copy(players = team.players.map(_.move()))

  extension (player: Player)
    /** Applies the Take action, giving the ball to the player if present.
      *
      * @return
      *   the updated Player with the ball, if taken
      */
    def updateBallPossession(): Player =
      player.nextAction match
        case Take(ball) => player.copy(ball = Some(ball))
        case _          => player

    /** Translates the nextAction of the player into movement and state changes.
      *
      * @return
      *   the updated Player
      */
    def processAction(): Player = player.nextAction match
      case Hit(_, _) =>
        player.copy(movement = Movement.still, ball = None, nextAction = Stopped(MatchConfig.stoppedAfterHit))
      case Move(direction, speed) => player.copy(movement = Movement(direction, speed))
      case Stopped(duration)      => player.copy(movement = Movement.still)
      case Take(ball)             => player.copy(movement = Movement.still)
      case _                      => player

  extension (ball: Ball)
    /** Updates the ball’s movement and optionally its position based on the ball carrier’s action.
      *
      * @param carrier
      *   the optional player controlling the ball
      * @return
      *   the updated Ball
      */
    def updateMovement(carrier: Option[Player]): Ball = carrier match
      case Some(Player(_, _, _, _, _, Hit(direction, speed))) => ball.copy(movement = Movement(direction, speed))
      case Some(Player(_, position, _, _, _, Move(direction, speed))) =>
        val movement    = Movement(direction, speed)
        val newPosition = position + (movement * (UIConfig.ballSize / 2))
        ball.copy(position = newPosition.clampToField, movement = movement)
      case Some(Player(_, position, movement, _, _, Take(ball))) =>
        ball.copy(position = position + (movement * (UIConfig.ballSize / 2)), movement = movement)
      case _ => ball

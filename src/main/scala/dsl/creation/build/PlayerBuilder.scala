package dsl.creation.build

import model.Match.*

/** Mutable DSL helper to describe a single [[model.Match.Player]] that will later be inserted in a generated match.
  *
  * Chainable methods (`at`, `move`, `ownsBall`, …) modify internal state and return the same builder so that calls can
  * be written fluently. Once configuration is complete, call [[build]] to obtain the immutable player value.
  *
  * @param id
  *   identifier unique within the scenario
  */
final class PlayerBuilder(id: Int):
  private var position: Position = Position(0, 0)
  private var motion: Movement   = Movement.still
  private var withBall: Boolean  = false
  private var decision: Decision = Decision.Initial
  private var action: Action     = Action.Initial

  /** Sets the player's position on the field.
    *
    * @param x
    *   horizontal coordinate (0 is left side, increases to the right)
    * @param y
    *   vertical coordinate (0 is top, increases downward)
    * @return
    *   this builder for method chaining
    */
  def at(x: Int, y: Int): PlayerBuilder =
    position = Position(x, y)
    this

  /** Sets the player's movement vector.
    *
    * @param dir
    *   direction of movement
    * @param speed
    *   magnitude of movement (units per tick)
    * @return
    *   this builder for method chaining
    */
  def move(dir: Direction)(speed: Int): PlayerBuilder =
    motion = Movement(Direction(dir.x, dir.y), speed)
    this

  /** Controls whether this player is in possession of the ball.
    *
    * @param hasBall
    *   true if player should start with the ball
    * @return
    *   this builder for method chaining
    */
  def ownsBall(hasBall: Boolean): PlayerBuilder =
    withBall = hasBall
    this

  /** Sets the player's initial decision making state.
    *
    * @param playerDecision
    *   the decision state to start with
    * @return
    *   this builder for method chaining
    */
  def decidedTo(playerDecision: Decision): PlayerBuilder =
    decision = playerDecision
    this

  /** Sets the player's next planned action.
    *
    * @param playerAction
    *   the action the player will take next
    * @return
    *   this builder for method chaining
    */
  def isGoingTo(playerAction: Action): PlayerBuilder =
    action = playerAction
    this

  /** Constructs an immutable Player instance with all configured properties.
    *
    * @return
    *   a new Player instance with the specified configuration
    */
  def build(): Player =
    val ball = if withBall then Some(Ball(position)) else None
    Player(id, position, motion, ball, decision, action)

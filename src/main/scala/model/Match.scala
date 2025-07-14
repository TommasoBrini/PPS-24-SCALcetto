package model

import dsl.SpaceSyntax.+

/** Root namespace that groups every data type needed to **describe the state of a football match** (players, ball,
  * score…). It also re-exports the geometry primitives from [[model.Space]] so that DSL users can access them without
  * an extra import.
  */
object Match:
  export Space.*

  /** Fine-grained **actions** a player or the ball can perform during a single simulation step.
    */
  enum Action:
    case Initial
    case Stopped(remainingStep: Int)
    case Move(direction: Direction, speed: Int)
    case Hit(direction: Direction, speed: Int)
    case Take(ball: Ball)

  /** **Intentions** lasting multiple ticks (run, pass, shoot…). They are produced by the **decision** layer and
    * decomposed into concrete [[Action]]s by the action layer.
    */
  enum Decision:
    case Initial
    case Confusion(remainingStep: Int)
    case Run(direction: Direction, steps: Int)
    case Pass(from: Player, to: Player)
    case Shoot(striker: Player, goal: Position)
    case MoveToGoal(goalDirection: Direction)

    case Mark(defender: Player, target: Player, teamSide: Side)
    case Tackle(ball: Ball)
    case Intercept(ball: Ball)
    case MoveToBall(directionToBall: Direction)

    case MoveRandom(direction: Direction, steps: Int)
    case ReceivePass(ball: Ball)

  /** Alias for the unique identifier attached to every [[Player]].
    */
  type ID = Int

  /** Immutable representation of an on-field **player**.
    *
    * @param id
    *   unique identifier (per scenario)
    * @param position
    *   current coordinates
    * @param movement
    *   current displacement vector (may be `still`)
    * @param ball
    *   `Some` if the player is controlling the ball
    * @param decision
    *   intention being executed
    * @param nextAction
    *   concrete action scheduled for the next step
    */

  trait Entity:
    def position: Position

  trait Moving[MovingEntity <: Entity]:
    self: MovingEntity =>
    def movement: Movement
    def withPosition(position: Position): MovingEntity
    def move(): MovingEntity = withPosition(position + movement)

  case class Player(
      id: ID,
      position: Position,
      movement: Movement = Movement.still,
      ball: Option[Ball] = None,
      decision: Decision = Decision.Initial,
      nextAction: Action = Action.Initial
  ) extends Entity with Moving[Player]:
    override def withPosition(position: Position): Player = copy(position = position)

  case class Ball(position: Position, movement: Movement = Movement.still) extends Entity with Moving[Ball]:
    override def withPosition(position: Position): Ball = copy(position = position)

  /** Logical side of the pitch a team defends. */
  enum Side:
    case West, East

  /** Group of players belonging to the same side.
    *
    * @param players
    *   squad list
    * @param side
    *   defended half
    * @param hasBall
    *   whether the team owns the ball at the moment
    */
  case class Team(players: List[Player], side: Side = Side.West, hasBall: Boolean = false)

  /** Opaque wrapper around the pair *(westGoals, eastGoals)* to prevent accidental swaps in client code.
    */
  opaque type Score = (Int, Int)
  object Score:
    def apply(scoreWest: Int, scoreEast: Int): Score = (scoreWest, scoreEast)
    def init(): Score                                = (0, 0)

  extension (score: Score)
    def westScore: Int = score._1
    def eastScore: Int = score._2

  /** Top-level immutable value that captures the **complete match state** at a given step.
    *
    * @param teams
    *   pair (`west`, `east`)
    * @param ball
    *   live ball
    * @param score
    *   current scoreline
    */
  case class Match(teams: (Team, Team), ball: Ball, score: Score = Score.init())

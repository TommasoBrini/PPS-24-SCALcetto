---
title: Rattini Emiliano
nav_order: 2
parent: Implementazione
---
# Rattini Emiliano

Nella mia parte mi sono concentrato in particolare sulla rappresentazione dello spazio, sull'implementazione della fase
di *act* e sull'integrazione delle tre fasi successive.
# Space
A livello di spazio è stato necessario modellare:
- *Position*: coppia di interi rappresentante la posizione.
- *Direction*: coppia di interi rappresentante l'offset da applicare a *Position* per modificarla.
- *Movement*: composto da *Direction* e da un intero che rappresenta la velocità.

Ho inoltre aggiunto un extension method per applicare il movimento alla posizione, modificandola 
sommandole la direzione moltiplicata per la velocità
```scala
  extension (p: Position)
    @targetName("applyMovement")
    def +(m: Movement): Position = calculateMovedPosition(p, m)
```

# Act
Per *Act* si intende la fase in cui le azioni di gioco (prendere la palla, tirare, muoversi) avvengono,
modificando lo stato della partita e ritornando un evento opzionale.
Il comportamento è il seguente:
```scala
  def actStep: State[Match, Option[Event]] = 
    State(state => {
      val updatedState = state
        .applyIf(existsSuccessfulTackle)(_.tackleBallCarrier())
        .applyIf(isPossessionChanging)(_.updateBallPossession())
        .updateMovements()
        .moveEntities()
      (updatedState, updatedState.detectEvent())
    })
```
Ho aggiunto un metodo condizionale che applica la funzione al *Match* solo se viene soddisfatto un predicato,
per aumentare la leggibilità del flusso.
I 4 momenti della fase di act sono stati organizzati nel seguente modo:
- **Tackling** - Se un tackle è avvenuto con successo, tolgo la palla al portatore e lo fermo per qualche giro
- **PossessionChange** - Se il possesso sta cambiando, ovvero qualcuno sta prendendo la palla, do la palla a chi la sta prendendo e 
aggiorno il flag del possesso dentro la squadra; è necessario non farlo sempre perchè avrei situazioni in cui
nessuna delle due squadre è in possesso palla, creando problemi con i comportamenti
- **MovementsUpdate** - Aggiorno i movimenti di tutte le entità, risolvendo le altre azioni del player
- **PositionsUpdate** - Aggiorno le posizioni applicando il movimento

Da qui, si sono rese necessarie 5 azioni per gestire correttamente le varie casistiche:
- *Move*: composta da direzione e velocità, muove il giocatore ed eventuamente la palla se il giocatore è in possesso
- *Hit*: composta da direzione e velocità, cambia movimento alla palla e la toglie al giocatore in possesso
- *Take*: assegna la palla al giocatore
- *Initial*: azione nulla, data alla creazione del giocatore
- *Stopped*: ferma il giocatore per un numero di giri

Le implementazioni dei metodi precedenti sono state raggruppate in *ActionProcessor* come extension methods.
Ecco tre esempi:
```scala
  extension (state: Match)
    def updateMovements(): Match =
      val carrier = state.players.find(_.hasBall)
      val teams   = state.teams.map(_.processActions())
      val ball    = state.ball.updateMovement(carrier)
      state.copy(teams = teams, ball = ball)

  extension (player: Player)
    def processAction(): Player = player.nextAction match
      case Hit(_, _) => 
        player.copy(movement = Movement.still, ball = None, nextAction = Stopped(MatchConfig.stoppedAfterHit))
      case Move(direction, speed) => player.copy(movement = Movement(direction, speed))
      case Stopped(duration)      => player.copy(movement = Movement.still)
      case Take(ball)             => player.copy(movement = Movement.still)
      case _                      => player

  extension (ball: Ball)
    def updateMovement(carrier: Option[Player]): Ball = carrier match
      case Some(Player(_, _, _, _, _, Hit(direction, speed))) => 
        ball.copy(movement = Movement(direction, speed))
      case Some(Player(_, position, _, _, _, Move(direction, speed))) =>
        val movement    = Movement(direction, speed)
        val newPosition = position + (movement * (UIConfig.ballSize / 2))
        ball.copy(position = newPosition.clampToField, movement = movement)
      case Some(Player(_, position, movement, _, _, Take(ball))) =>
        ball.copy(position = position + (movement * (UIConfig.ballSize / 2)), movement = movement)
      case _ => ball
```
In quest'ultimo metodo c'è un piccolo workaround per non fare uscire la palla dal campo essendo essa davanti al giocatore
e potendo il giocatore raggiungere il bordo.
Qualche test usando *AnyFlatSpec* e *Matchers*:
```scala
  "A player" should "gain possession of the ball if he's taking it" in:
    val ball   = Ball(Position(0, 0))
    val player = Player(0, Position(0, 0), ball = Some(ball), nextAction = Take(ball))
    player.updateBallPossession().ball should be(Some(ball))

  it should "move when he has to" in:
    val player = Player(0, Position(0, 0), nextAction = Move(defaultDirection, defaultSpeed))
    player.processAction().movement should be(Movement(defaultDirection, defaultSpeed))

  it should "stand still if he is stopped" in:
    val player = Player(0, Position(0, 0), nextAction = Stopped(1))
    player.processAction().movement should be(Movement.still)

  it should "move correctly" in:
    val initialPosition = Position(0, 0)
    val initialMovement = Movement(defaultDirection, defaultSpeed)
    val player          = Player(0, initialPosition, movement = initialMovement)
    player.move().position should be(initialPosition + initialMovement)
```
# Update Flow
Come si può aver notato dal primo snippet sul comportamento della fase di *Act*, le varie fasi del ciclo di *Update*
sono state modellate come variazioni di stato usando lo *State* del laboratorio 4.
```scala
  case class State[S, A](run: S => (S, A))
```
In questo caso il valore prodotto viene usato solo nella *Act*, che ritorna un *Event* che rappresenta l'evento di 
goal, uno per squadra, e l'evento di palla uscita.
```scala
  enum Event:
    case BallOut, GoalEast, GoalWest

  def update(state: Match): Match =
    val updateFlow: State[Match, Option[Event]] = 
      for
        _     <- decideStep
        _     <- validateStep
        event <- actStep
      yield event
      val (updated, event) = updateFlow.run(state)
      handleEvent(updated, event)
```
L'evento di goal viene gestito aumentato lo *Score* dentro lo stato mentre quello di *BallOut* attraverso
la modifica del movimento della palla con un rimbalzo.
```scala
  import Event.*
  private def handleEvent(state: Match, event: Option[Event]): Match =
    event match
      case Some(BallOut) =>
        val bounceType = state.ball.position getBounce (fieldWidth, fieldHeight)
        state.copy(ball = state.ball.copy(movement = state.ball.movement getMovementFrom bounceType)) 
      case Some(GoalEast) =>
        println("East Goal!!!")
        SituationGenerator.kickOff(state.score.eastGoal, West) 
      case Some(GoalWest) =>
        println("West Goal!!!")
        SituationGenerator.kickOff(state.score.westGoal, East) 
      case _ => state
```


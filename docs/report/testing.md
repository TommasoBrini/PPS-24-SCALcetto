---
title: Testing
nav_order: 7
parent: Report
---
# Testing
L'approccio al testing nel progetto ha adottato il **Test-Driven Development (TDD)** come metodologia principale, 
scrivendo test prima dell'implementazione per guidare il design e verificare le funzionalità. 
Inizialmente, si è proceduto con test su funzioni private importandole tramite visibilità di package 
in Scala, ma questa pratica è stata abbandonata in favore di **test focalizzati su API pubbliche** e 
comportamenti complessivi, per evitare test fragili legati a dettagli interni. 
La **copertura** del codice è stata integrata nella **pipeline CI/CD** tramite tool SBT, 
con report generati e caricati su **Codecov** per monitorare metriche complete su componenti model, 
update, view e DSL

## Evoluzione della Strategia di Testing
Il TDD ha iniziato con test unitari granulari, inclusi tentativi di validare logiche interne come 
decisioni nei moduli di update, importando metodi privati per asserzioni specifiche. 
Tuttavia, questo è stato ritenuto inefficiente poiché legava i test a implementazioni 
che potevano cambiare senza impattare i comportamenti esterni. La strategia si è evoluta 
verso test comportamentali, enfatizzando interazioni realistiche, come decisioni dei giocatori in
stati di partita o rendering della view sotto cambiamenti di stato, 
migliorando la robustezza e coprendo casi limite come team vuoti.

## Copertura del Codice e Integrazione CI
La copertura è stata applicata tramite un workflow GitHub Actions che esegue test su Ubuntu, utilizzando SBT per pulire, testare e generare report, 
con upload XML su Codecov per tracciare linee e branch non coperte.
Questo setup previene regressioni e mantiene alta copertura.

## Codice
I test comportamentali sono stati implementati con **ScalaTest's** **FlatSpec** e **Matchers** per asserzioni leggibili. 

- Ecco un esempio dai test della view, che verifica inizializzazione e rendering senza eccezioni, questo test si concentra su comportamenti osservabili in ambienti con interfaccia grafica,
  saltando test GUI se in ambienti headless per gestire vincoli CI:
```scala
"SwingView" should "initialize with the correct panels and buttons" in:
  val state = SituationGenerator.kickOff(Score.init())
  val view = new SwingView(state)
  noException should be thrownBy view.render(state)
```
- Nei test del model, la creazione di decisioni è validata comportamentalmente, ciò garantisce che le decisioni corrispondano a tipi e parametri attesi senza approfondire stati privati:
```scala
"A Player" should "can decide to run" in:
  val player = Player(1, Position(0, 0))
  val direction = player.position.getDirection(Position(1, 0))
  val steps = config.MatchConfig.runSteps
  val decision = player.createRunDecision(direction, steps)
  decision shouldBe a[Decision.Run]
  val run = decision.asInstanceOf[Decision.Run]
  run.direction shouldBe direction
  run.steps shouldBe steps
```

- I test della logica di update asseriscono cambiamenti di stato olistici, come aggiornamenti di decisioni su più giocatori, 
utilizzando il DSL di creazione sviluppato per l'inizializzazione di stati della partita:
```scala
"Decide.decide" should "update all players with new decisions" in:
  val state: Match = newMatch(Score.init()):
    team(West) withBall:
      player(1) at (5, 5) ownsBall true
      player(3) at (15, 15)
    team(East):
      player(2) at (10, 10)
    ball at (0, 0) move (Direction(0, 0), 0)
  val (updatedState, _) = decideStep.run(state)
  updatedState.teams.players.foreach { player =>
    player.decision should not be Decision.Initial
  }
```



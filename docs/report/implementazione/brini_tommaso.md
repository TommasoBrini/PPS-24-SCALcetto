---
title: Brini Tommaso
nav_order: 1
parent: Implementazione
---
# Brini Tommaso
Il mio contributo al progetto si concentra sulla fase di **decisione** all'interno del ciclo *decide-validate-act*, implementando un sistema di decision-making intelligente e modulare per i giocatori. L'architettura segue i principi di clean code e programmazione funzionale, garantendo il più possibile separazione delle responsabilità e flessibilità nell'estensione dei comportamenti dei giocatori.

## Panoramica
Il sistema di decisione è strutturato in diversi livelli di astrazione:
- **Decide** => Orchestratore principale che coordina l'intero processo decisionale per tutti i giocatori. E' l'entry point per la fase di decisione.
- **DecisionMaker** => nucleo del sistema che in base al tipo del giocatore specifico richiama la giusta logica di selezione delle decisioni ottimali.
- **PlayerRoleFactory** e **PlayerTypes** => Sistema di mixin che definisce i ruoli dei giocatori con capacità decisionali specifiche.
- **Decisions** => Trait che definiscono le capacità decisionali specifiche per ogni ruolo di giocatore
- **Behavior** => Moduli specializzati che valutano lo stato del match e guidano la selezione delle decisioni.
- **BallCarrierDecisionRating** => Sistema di rating che assegna punteggi alle decisioni del giocatore in possesso palla basandosi su criteri strategici e uniformi

## Architettura del Sistema di Decisione

Il sistema è progettato seguendo un approccio modulare, dove ogni tipo di giocatore viene creato tramite mixin di trait che ne definiscono le capacità decisionali. Questo permette di estendere facilmente i comportamenti e di mantenere il codice organizzato e manutenibile.

All'interno del Model, le possibili decisioni sono state implementate come una enum che rappresenta tutte le azioni disponibili per i giocatori:

<figure class="w-5 mx-auto">
  <img src="../assets/images/decision/decision.png" alt="Decision Enum Structure">
  <figcaption>Struttura dell'enum Decision</figcaption>
</figure>

Di seguito verranno spiegati nel dettaglio le principali componenti

## Decide
La componente *Decide* rappresenta l'orchestratore principale della fase di decisione all'interno del ciclo decide-validate-act. Il suo compito è coordinare il processo decisionale di tutti i giocatori in modo dichiarativo, modulare e privo di *side-effects*, in linea con i principi della programmazione funzionale.
Si basa sull'uso della **State Monad**, che consente di trasformare lo stato della partita in modo puro e trasparente, senza mutare direttamente gli oggetti.
<figure class="w-5 mx-auto">
  <img src="../assets/images/decision/decide.png" alt="Decide Monad State">
  <figcaption>Struttura dell'entry point</figcaption>
</figure>

L'intero processo è suddiviso in funzioni private, ognuna con una responsabilità ben definita, favorendo la leggibilità e la manutenibilità del codice.
Il flusso di Decide è il seguente:
- Assegna a ciascun giocatore il proprio ruolo tramite mixin, in base alla situazione corrente
- Determina quale squadra è in attacco e quale in difesa, per poi assegnare le marcature ai difensori, cioè il riferimento dell'attaccante da marcare.
- Aggiorna le decisioni di tutti i giocatori, delegando la logica specifica al metodo *decide* di ciascun giocatore

Il metodo *decide* è implementato come extension method su **Player**, all'interno del modulo **DecisionMaker**. Questo modulo sfrutta il pattern *type class* tramite trait e mixin, delegando la scelta della decisione ottimale al comportamento specifico del ruolo del giocatore.
- Se il giocatore è un BallCarrierPlayer, la decisione viene calcolata dal modulo BallCarrierBehavior.
- Se è un OpponentPlayer, la logica viene delegata a OpponentBehavior, tenendo conto delle marcature.
- Se è un TeammatePlayer, la decisione viene affidata a TeammateBehavior.

<figure class="w-5 mx-auto">
  <img src="../assets/images/decision/DecisionMaker.png" alt="DecisionMaker">
  <figcaption>Decision Maker</figcaption>
</figure>

Questa struttura permette di aggiungere facilmente nuovi ruoli o comportamenti, mantenendo il codice aperto all'estensione ma chiuso alla modifica (*Open/Close Principle*). 
Infatti, nel caso volessimo aggiungere nuovi ruoli (innanzitutto, il portiere!), è sufficiente aggiungere nuovi trait per le capacità, creare un nuovo comportamento specifico e aggiungere un nuovo case in questo match case, senza modificare la logica esistente per gli altri ruoli
L'uso di pattern matching esaustivo e la composizione di funzioni pure garantiscono robustezza e chiarezza.

## PlayerTypes
La gestione dei ruoli dei giocatori è uno degli aspetti chiave per mantenere il sistema estendibile, leggibile e robusto. In questa architettura, i ruoli vengono definiti tramite **mixin** di trait.
Il modulo PlayerTypes definisce, tramite type alias, i diversi ruoli che un giocatore può assumere durante la partita. Ogni ruolo è una composizione di trait che rappresentano le capacità decisionali specifiche per quel ruolo.
<figure class="w-5 mx-auto">
  <img src="../assets/images/decision/playerTypes.png" alt="PlayerTypes">
  <figcaption>Player Types</figcaption>
</figure>
Garantisce:
- *Type safety*: ogni ruolo ha solo le capacità che gli competono.
- *Chiarezza*
- *Estendibilità*: aggiungere un nuovo ruolo o una nuova capacità a un ruolo già presente richiede solo la definizione di un nuovo trait e l'aggiornamento del type alias.

Il modulo PlayerRoleFactory fornisce delle extension methods che permettono di trasformare un oggetto Player generico in una delle sue specializzazioni di ruolo, incapsulando la logica di creazione dei ruoli. In questo modo, mantiene il codice pulito e la creazione di nuovi ruoli rimane molto semplice e coerente.

Viene implementato anche un modulo per definire quali possibili decisioni può prendere uno specifico tipo di giocatore. Per esempio, questa funzione definisce quali Decisioni può selezionare il giocatore in possesso di palla
```scala
  extension (player: PlayerTypes.BallCarrierPlayer)
    def generateAllPossibleDecisions(state: Match): List[Decision] =
      player.decision match
        case Decision.Initial => player.generatePossiblePasses(state)
        case _ =>
          player.generatePossibleRunDirections(state) ++
            player.generatePossiblePasses(state) ++
            player.generatePossibleShots(state) ++
            player.generatePossibleMovesToGoal(state)
```

## Decisions
La componente Decisions raccoglie tutti i trait che rappresentano le **capacità decisionali** dei diversi ruoli. Ogni trait incapsula una specifica azione che un giocatore può compiere, secondo il principio di single responsability e favorendo la composizione funzionale.
Questa struttura permette di:
- Comporre ruoli complessi a partire da capacità semplici e riutilizzabili
- Estendere facilmente il sistema aggiungendo nuove capacità senza modificare il codice esistente
- Garantire che ogni giocatore abbia solo le azioni che prevede il suo ruolo

Un esempio rappresentativo è il trait *CanDecideToPass*, che fornisce a un giocatore la capacità di generare decisioni di passaggio e di restituire tutti i possibili passaggi ai compagni:
<figure class="w-5 mx-auto">
<img src="../assets/images/decision/canDecideToPass.png" alt="Example of Trait">
<figcaption>Esempio di trait decisionale: CanDecideToPass</figcaption>
</figure>
- Il trait può aggiunto come mixin a qualsiasi classe che estende Player
- Fornisce sia la creazione di una singola decisione di passaggio, sia la generazione di tutte le possibili opzioni di passaggio ai compagni
- L'implementazione è completamente *side-effects free* e facilmente testabile

Ho implementato questa architettura poichè mi garantisce composizione (ogni ruolo è una combinazione di questi trait riutilizzabili), estendibilità, estrema pulizia (ogni trait è piccolo e ben documentato) 

## Behavior
La componente Behavior incapsula la logica decisionale specifica per ciascun ruolo di giocatore, separando nettamente le strategie di gioco in base al contesto e al ruolo. Ogni modulo di comportamento implementa un metodo *calculateBestDecision*, che valuta lo stato corrente della partita e restituisce la decisione ottimale per il giocatore

### OpponentBehavior
Comportamento dei giocatori avversari. La logica è articolata e si basa sull'analisi della situazione difensiva.
Ho creato una enum privata che incapsula tutte le possibili situazioni difensive, in modo da rendere il codice più chiaro e estendibile
```scala
private enum DefensiveSituation:
  case BallCarrierInTackleRange(ballCarrier: Player)
  case BallInInterceptRange
  case BallInProximityRange
  case NoImmediateThreat
```
In base alla situazione riconosciuta, verrà selezionata la Decisione difensiva corretta, secondo queste priorità:
- Tackle se molto vicino al portatore di palla
- Intercetto se molto vicino alla palla vagante
- Movimento verso la palla se in prossimità di essa
- Marcatura di un avversario assegnato

### TeammateBehavior
Anche per il comportamento dei compagni di squadra mi sono basato su una valutazione della situazione, sempre rappresentata come un enum privata

```scala
private enum TeammateSituation:
  case Confusion(remainingSteps: Int)
  case BallInInterceptRange
  case BallInProximityRange
  case ContinueMovement(direction: Direction, remainingSteps: Int)
  case RandomMovement
```
In questo caso, la calculateBestDecision è molto semplice
```scala
def calculateBestDecision(state: Match): Decision =
    val situation = analyzeSituation(player, state)
    takeDecision(player, situation, state)
```      
Per il comportamento dei compagni, ho seguito questo sistema di priorità:
- Se molto vicino alla palla vagante, si prepara a riceverla
- Se in prossimità della palla vagante, si muove verso essa
- Altrimenti, si muove casualmente all'interno del campo.

N.B. Con questa architettura è molto semplice creare tattiche e strategie di movimento per le squadre, basterebbe modificare o estendere il movimento casuale dei compagni

### BallCarrierBehavior
Per il giocatore in possesso palla ho deciso di implementare una dinamica diversa. Una volta generato tutte le possibili decisioni, le valuta tramite un sistema di rating e seleziona quella con punteggio più alto
```scala
  private def selectBestDecision(player: BallCarrierPlayer, state: Match): Decision =
    val possibleDecisions = player.generateAllPossibleDecisions(state)
    val decisionRatings   = rateAllDecisions(possibleDecisions, player, state)
    selectHighestRatedDecision(decisionRatings)
``` 
Questo approccio è funzionale: tutte le funzioni sono pure, senza effetti collaterali, e la selezione della decisione migliore avviene tramite funzioni di ordine superiore e strutture dati immutabili.

## BallCarrierDecisionRating
Questo modulo implementa il sistema di valutazione (rating) delle decisioni per il giocatore in possesso di palla. Questo sistema assegna un punteggio numerico a ciascuna possibile azione, permettendo di selezionare in modo oggettivo e trasparente la decisione più vantaggiosa in ogni situazione.

Per ogni tipo di decisione viene definita una extension method che restituisce un voto compreso tra 0 a 1, descritto da questa struttura
```scala
private object RatingValues:
  val Excellent: Double    = 1.0
  val Good: Double         = 0.8
  val Average: Double      = 0.7
  val Poor: Double         = 0.5
  val VeryPoor: Double     = 0.2
  val VeryVeryPoor: Double = 0.1
  val Impossible: Double   = 0.0
```
**Esempio: rating di un passaggio.**
La valutazione di un passaggio tiene conto di
- *strada libera* -> nessun avversario in traiettoria
- *distanza* -> passaggi troppo lunghi sono penalizzati
- *avanzamento* -> i retropassaggi sono penalizzati rispetto ai passaggi filtranti

**Vantaggi**
- Oggettività: le decisioni sono valutate secondo criteri chiari e uniformi
- Estendibilità: nuovi criteri di valutazione possono essere aggiunti facilmente, modificando solo le funzioni di rating
- Pulizia e testing: ogni funzione di rating è pura, facilmente testabile e documentata


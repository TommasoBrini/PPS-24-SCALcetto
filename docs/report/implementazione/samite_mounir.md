---
title: Samite Mounir
nav_order: 3
parent: Implementazione
---
# Samite Mounir

## Creation DSL
Il Creation DSL è un mini-linguaggio embedded in Scala che consente agli autori di scenari di dichiarare l’intera configurazione di una partita di calcio in poche linee quasi naturali.
Gli obiettivi dietro sono:
- **Nascondere il Modello**: si può manipolare lo stato senza andare a chiamare direttamente i costruttori del modello.
- **Imporre correttezza**: possono essere creati solo due team, un unico pallone e ID giocatore univoci.
- **Preservare l’immutabilità**: gli oggetti finali Match, Team, Player e Ball sono completamente immutabili, pur offrendo una fase di configurazione mutabile e fluente.

Internamente il DSL è costruito attorno a una famiglia di builder più due facciate (CreationSyntax e SituationGenerator).
Le sezioni seguenti descrivono ogni componente in dettaglio.

### Builders
- Offrono *setter chainable* che modificano variabili private e restituiscono lo stato corrente del builder con `this`.
- build() produce un istanza immutabile della componente in costruzione alla fine della concatenazione di caratteristiche.
#### MatchBuilder
- Radice della gerarchia: ogni descrizione di partita inizia istanziando MatchBuilder. 
- Mantiene un singleton BallBuilder per garantire l’esistenza di un solo pallone. 
- Verifica che siano forniti esattamente due team, sollevando IllegalArgumentException in caso contrario. 

#### TeamBuilder
- Accumula istanze di PlayerBuilder in un *ListBuffer* fino alla chiamata di *build()*.
- withBall è sia un flag sia un aiuto di scoping: registra il possesso iniziale e consente un blocco di configurazione interno.
- Il metodo apply rende il builder stesso un receiver implicito, permettendo di omettere il riferimento esplicito a `this`.

### Le Context Functions nel DSL
Le Context Functions introdotte da Scala 3 sono il meccanismo chiave che consente al DSL di apparire “magicamente” aperto sul builder corretto senza doverlo passare a mano fra tutte le chiamate.
#### Come funziona
1. Il valore scope viene marcato come given e quindi diventa disponibile per tutte le funzioni che richiedono un using MatchBuilder nel corpo del blocco passato a newMatch.
```scala
given scope: MatchBuilder = MatchBuilder(score)      // dentro newMatch
```
2. Ogni volta che si invoca `team(side)` non è necessario passare il MatchBuilder: il compilatore lo pesca dal contesto corrente.
3. All’interno di `team(side)` viene creato un TeamBuilder; la sua istanza viene poi resa implicita nello scope del blocco:
```scala
def apply(body: TeamBuilder ?=> Unit): TeamBuilder =
  body(using this)        // `this` diventa il context value TeamBuilder
```

Grazie a ciò, il successivo player(id) trova automaticamente il TeamBuilder corretto.
4. Poiché ogni builder è scope-bound, non può “uscire” dal proprio contesto e contaminare altri team o match, inoltre il compilatore impedisce di chiamare player fuori da un blocco di team, o team fuori da newMatch.

#### Motivazione 
- Sintassi pulita: elimina parametri ripetitivi come istanze di `MatchBuilder` e `TeamBuilder`
- Tipi verificati: se si tenta di usare una funzione senza il context value corretto, l’errore viene rilevato a compile-time.
- Scope esplicito ma non verboso: la struttura dei blocchi `( newMatch: team: player … )` riflette la gerarchia degli oggetti costruiti, facilitando la lettura.


### Sintassi
#### CreationSyntax
Una facciata che cuce insieme i builder tramite clausole using contestuali:

```scala
newMatch(score):                        // MatchBuilder implicito
  team(West):                           // TeamBuilder implicito
    player(1) at (3,4) ownsBall true
  team(East): 
    player(2) at (3,4) ownsBall false
  ball at (50,25) move (Direction(0,0), 0)
```

Punti chiave:
- newMatch restituisce il Match già costruito, rendendolo l’unico punto di ingresso.
- i metodi team, ball e player sono proxy che delegano il lavoro al builder necessario.
- Il supporto all’operatore postfix dona una sintassi più scorrevole `(“ownsBall true”, “move (dir, speed)”)`.

### Principi di Progettazione
- Builder Pattern: isola la mutabilità, mantenendo l’API esterna immutabile.
- Fluent Interface: ogni mutatore restituisce this, permettendo codice dallo stile naturale.
- Contextualità con using: riduce la verbosità nei parametri; i builder “appaiono” dove servono.

### Benefici Concreti
- **Scripting rapido di scenari da zero**: il DSL consente di generare qualunque configurazione con poche righe leggibili.
- **Un solo codice, due utilizzi**: La stessa creazione di scenario viene utilizzata sia nei test che nella simulazione effettiva, 
in questo modo si riesce a testare il codice in situazioni più realistiche di come avviene la simulazione aumentando in maniera significativa la qualità/utilità dei test.
- **Esplorazione delle feature avanzate di Scala 3**: nel progettare questa sezione di codice ho considerato anche il fatto che tra gli obiettivi del progetto
  c'era anche quello di interfacciarsi alle funzionalità avanzate di programmazione funzionale e scala.
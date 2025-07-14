---
title: Retrospettiva
nav_order: 8
parent: Report
---
# Retrospettiva

## Sprint: first step

### Descrizione dell'andamento dello sviluppo

Questo sprint iniziale ha posto le basi per il progetto, concentrandosi sulla configurazione dell'ambiente di sviluppo e sull'integrazione continua. 
Sono state completate attività chiave come il setup della CI con semantic release e formattazione del codice, nonché la creazione di pagine GitHub per la documentazione. 
Lo sviluppo è proceduto senza intoppi, con tutte le task risolte entro pochi giorni, garantendo una solida fondazione per gli sprint successivi.

### Backlog
Il backlog comprendeva task essenziali per l'avvio:
- Setup della CI, inclusi semantic release e code formatting (completato in 3 ore da Brini Tommaso).
- Configurazione delle docs su GitHub Pages e setup del progetto (completato in 3 ore da Samite Mounir).


### Iterazioni

Le iterazioni sono state brevi e focalizzate, con aggiornamenti quotidiani che hanno permesso di risolvere rapidamente le task senza ritardi. Non sono emersi impedimenti significativi, e il team ha mantenuto un ritmo costante attraverso pair programming implicito nelle assegnazioni.

### Commenti sprint

Sprint efficace per l'inizializzazione, con benefici in termini di automazione e documentazione. Ha facilitato transizioni fluide agli sprint successivi, sebbene la stima del tempo sia stata conservativa.

## Sprint: first release

### Descrizione dell'andamento dello sviluppo

Questo sprint ha visto un progresso significativo nella modellazione e nell'implementazione di componenti core, come il refactoring del model, l'implementazione di decisioni per i giocatori e l'aggiunta di test per la copertura. La maggior parte delle task è stata completata, con enfasi sul pair programming per risolvere complessità nel model e nella GUI. Sono state gestite circa 10 task, con tempi di esecuzione variabili da 1 a 8 ore, culminando in una base funzionale per la simulazione.

### Backlog

Il backlog includeva una varietà di task di implementazione e refactoring:

- Implementazione di act (completato in 8 ore da Rattini Emiliano).
- Refactoring del model e GUI (completato in 3 ore da Brini Tommaso).
- Implementazione di decide (completato in 5 ore da Samite Mounir).
- Refactoring del progetto tramite pair programming (completati in 5 ore da Samite Mounir e in 3 ore da Brini Tommaso).
- Implementazione di decisioni per giocatore con palla (completato in 5 ore da Samite Mounir).
- Implementazione di move random e decide no control (completato in 6 ore da Brini Tommaso).
- Controllo della copertura test con badge Codecov (completato in 2 ore da Brini Tommaso).
- Pair programming per refactoring model (completato in 5 ore da Rattini Emiliano).

### Iterazioni

Le iterazioni hanno coinvolto sessioni di pair programming per task complesse, con aggiornamenti regolari che hanno ridotto i tempi di risoluzione. 
Sono state identificate e corrette dipendenze tra task, come il refactoring prima delle implementazioni, mantenendo un flusso iterativo senza blocchi maggiori.

### Commenti sprint

Sprint produttivo che ha rafforzato la struttura del simulatore, con un buon equilibrio tra sviluppo e testing. 
Il pair programming ha migliorato la qualità, ma future stime potrebbero beneficiare di una maggiore granularità per task ad alta complessità.
Purtroppo non siamo riusciti a raggiungere un buon risultato che rispettava gli obiettivi minimi della prima release del progetto, dunque è stato programmato uno sprint successivo più corto.

## Sprint: pre-first-release

### Descrizione dell'andamento dello sviluppo

Focalizzato su test, implementazioni e documentazione pre-rilascio, questo sprint ha completato task relative a model, decisioni e confini del gioco. 
Sono state implementate logiche per movimenti casuali, test di qualità e docs su MVU e architetture. L
a maggior parte delle attività è stata risolta, con tempi totali intorno ai 1-10 ore per task, preparando il terreno per il primo rilascio.

### Backlog

Il backlog copriva test, implementazioni e documentazione:

- Scrittura docs su introduzione e requisiti (completato in 2 ore da Rattini Emiliano).
- Implementazione confini (completato in 5 ore da Rattini Emiliano).
- Test per act e confini (completato in 2 ore da Rattini Emiliano).
- Scrittura docs su CICD (completato in 2 ore da Brini Tommaso).
- Scrittura docs su Step Decide Action (completato in 2 ore da Brini Tommaso).
- Riflessione su monadi per model (completato in 2 ore da Samite Mounir).
- Implementazione decisioni giocatore con palla (completato in 10 ore da Samite Mounir).
- Test per decide (completato in 3 ore da Samite Mounir).
- Test per model (completato in 2 ore da Brini Tommaso).
- Test per game initializer (completato in 2 ore da Brini Tommaso).
- Scrittura docs su MVU (completato in 3 ore da Samite Mounir).


### Iterazioni

Le iterazioni hanno enfatizzato test-driven-development, con cicli rapidi di implementazione e verifica. Gli aggiornamenti hanno evidenziato progressi in task correlate, come test post implementazione, riducendo rischi di regressioni.

### Commenti sprint

Sprint orientato alla qualità, con enfasi su docs e test che ha elevato lo standard del progetto. Ha rivelato opportunità per ottimizzare le stime, specialmente per task di riflessione e documentazione.

## Sprint: second release

### Descrizione dell'andamento dello sviluppo

Questo sprint ha avanzato funzionalità di gioco come decisioni opponenti, eventi goal e accuracy rates per azioni. 
Molte task sono state completate, inclusi refactoring e implementazioni in pair programming, con tempi da 4 a 10 ore. 
Ha costruito su sprint precedenti, migliorando la logica di simulazione verso un secondo rilascio.

### Backlog

Il backlog si concentrava su implementazioni e accuracy:

- Implementazione act con accuracy per tackle (completato in 10 ore da Brini Tommaso).
- Implementazione decisioni opponenti (completato in 6 ore da Brini Tommaso).
- Implementazione evento goal (completato in 5 ore da Samite Mounir).
- Implementazione decisioni ball player (shoot) (completato in 5 ore da Samite Mounir).
- Implementazione act con accuracy per shoot (completato in 5 ore da Samite Mounir).
- Implementazione team with ball decisions (take e move random) (completati in 8 ore da Brini Tommaso e in 6 ore da Rattini Emiliano).
- Implementazione ball player decisions (pass) (completato in 6 ore da Rattini Emiliano).
- Implementazione act con accuracy per pass (completato in 6 ore da Rattini Emiliano).
- Layer intermedio per accuracy via pair programming (completato in 8 ore da Samite Mounir).
- Refactoring mixin (completato in 10 ore da Brini Tommaso).


### Iterazioni

Le iterazioni hanno incluso pair programming per task complesse, con revisioni iterative che hanno affinato probabilità e logiche di gioco. Questo ha permesso adattamenti rapidi basati su feedback interni.

### Commenti sprint

Sprint dinamico che ha arricchito le meccaniche di gioco, con successi nel handling di accuracy. Suggerisce un maggiore focus su task aperte per chiudere loop di sviluppo.

## Sprint: functional refactor

## Sprint: first step

### Descrizione dell'andamento dello sviluppo

Questo sprint iniziale ha posto le basi per il progetto, concentrandosi sulla configurazione dell'ambiente di sviluppo e sull'integrazione continua. 
Sono state completate attività chiave come il setup della CI con semantic release e formattazione del codice, nonché la creazione di pagine GitHub per la documentazione. 
Lo sviluppo è proceduto senza intoppi, con tutte le task risolte entro pochi giorni, garantendo una solida fondazione per gli sprint successivi.

### Backlog
Il backlog comprendeva task essenziali per l'avvio:
- Setup della CI, inclusi semantic release e code formatting (completato in 3 ore da Brini Tommaso).
- Configurazione delle docs su GitHub Pages e setup del progetto (completato in 3 ore da Samite Mounir).


### Iterazioni

Le iterazioni sono state brevi e focalizzate, con aggiornamenti quotidiani che hanno permesso di risolvere rapidamente le task senza ritardi. Non sono emersi impedimenti significativi, e il team ha mantenuto un ritmo costante attraverso pair programming implicito nelle assegnazioni.

### Commenti sprint

Sprint efficace per l'inizializzazione, con benefici in termini di automazione e documentazione. Ha facilitato transizioni fluide agli sprint successivi, sebbene la stima del tempo sia stata conservativa.

## Sprint: first release

### Descrizione dell'andamento dello sviluppo

Questo sprint ha visto un progresso significativo nella modellazione e nell'implementazione di componenti core, come il refactoring del model, l'implementazione di decisioni per i giocatori e l'aggiunta di test per la copertura. La maggior parte delle task è stata completata, con enfasi sul pair programming per risolvere complessità nel model e nella GUI. Sono state gestite circa 10 task, con tempi di esecuzione variabili da 1 a 8 ore, culminando in una base funzionale per la simulazione.

### Backlog

Il backlog includeva una varietà di task di implementazione e refactoring:

- Implementazione di act (completato in 8 ore da Rattini Emiliano).
- Refactoring del model e GUI (completato in 3 ore da Brini Tommaso).
- Implementazione di decide (completato in 5 ore da Samite Mounir).
- Refactoring del progetto tramite pair programming (completati in 5 ore da Samite Mounir e in 3 ore da Brini Tommaso).
- Implementazione di decisioni per giocatore con palla (completato in 5 ore da Samite Mounir).
- Implementazione di move random e decide no control (completato in 6 ore da Brini Tommaso).
- Controllo della copertura test con badge Codecov (completato in 2 ore da Brini Tommaso).
- Pair programming per refactoring model (completato in 5 ore da Rattini Emiliano).

### Iterazioni

Le iterazioni hanno coinvolto sessioni di pair programming per task complesse, con aggiornamenti regolari che hanno ridotto i tempi di risoluzione. 
Sono state identificate e corrette dipendenze tra task, come il refactoring prima delle implementazioni, mantenendo un flusso iterativo senza blocchi maggiori.

### Commenti sprint

Sprint produttivo che ha rafforzato la struttura del simulatore, con un buon equilibrio tra sviluppo e testing. 
Il pair programming ha migliorato la qualità, ma future stime potrebbero beneficiare di una maggiore granularità per task ad alta complessità.
Purtroppo non siamo riusciti a raggiungere un buon risultato che rispettava gli obiettivi minimi della prima release del progetto, dunque è stato programmato uno sprint successivo più corto.

## Sprint: pre-first-release

### Descrizione dell'andamento dello sviluppo

Focalizzato su test, implementazioni e documentazione pre-rilascio, questo sprint ha completato task relative a model, decisioni e confini del gioco. 
Sono state implementate logiche per movimenti casuali, test di qualità e docs su MVU e architetture. L
a maggior parte delle attività è stata risolta, con tempi totali intorno ai 1-10 ore per task, preparando il terreno per il primo rilascio.

### Backlog

Il backlog copriva test, implementazioni e documentazione:

- Scrittura docs su introduzione e requisiti (completato in 2 ore da Rattini Emiliano).
- Implementazione confini (completato in 5 ore da Rattini Emiliano).
- Test per act e confini (completato in 2 ore da Rattini Emiliano).
- Scrittura docs su CICD (completato in 2 ore da Brini Tommaso).
- Scrittura docs su Step Decide Action (completato in 2 ore da Brini Tommaso).
- Riflessione su monadi per model (completato in 2 ore da Samite Mounir).
- Implementazione decisioni giocatore con palla (completato in 10 ore da Samite Mounir).
- Test per decide (completato in 3 ore da Samite Mounir).
- Test per model (completato in 2 ore da Brini Tommaso).
- Test per game initializer (completato in 2 ore da Brini Tommaso).
- Scrittura docs su MVU (completato in 3 ore da Samite Mounir).


### Iterazioni

Le iterazioni hanno enfatizzato test-driven-development, con cicli rapidi di implementazione e verifica. Gli aggiornamenti hanno evidenziato progressi in task correlate, come test post implementazione, riducendo rischi di regressioni.

### Commenti sprint

Sprint orientato alla qualità, con enfasi su docs e test che ha elevato lo standard del progetto. Ha rivelato opportunità per ottimizzare le stime, specialmente per task di riflessione e documentazione.

## Sprint: second release

### Descrizione dell'andamento dello sviluppo

Questo sprint ha avanzato funzionalità di gioco come decisioni opponenti, eventi goal e accuracy rates per azioni. 
Molte task sono state completate, inclusi refactoring e implementazioni in pair programming, con tempi da 4 a 10 ore. 
Ha costruito su sprint precedenti, migliorando la logica di simulazione verso un secondo rilascio.

### Backlog

Il backlog si concentrava su implementazioni e accuracy:

- Implementazione act con accuracy per tackle (completato in 10 ore da Brini Tommaso).
- Implementazione decisioni opponenti (completato in 6 ore da Brini Tommaso).
- Implementazione evento goal (completato in 5 ore da Samite Mounir).
- Implementazione decisioni ball player (shoot) (completato in 5 ore da Samite Mounir).
- Implementazione act con accuracy per shoot (completato in 5 ore da Samite Mounir).
- Implementazione team with ball decisions (take e move random) (completati in 8 ore da Brini Tommaso e in 6 ore da Rattini Emiliano).
- Implementazione ball player decisions (pass) (completato in 6 ore da Rattini Emiliano).
- Implementazione act con accuracy per pass (completato in 6 ore da Rattini Emiliano).
- Layer intermedio per accuracy via pair programming (completato in 8 ore da Samite Mounir).
- Refactoring mixin (completato in 10 ore da Brini Tommaso).


### Iterazioni

Le iterazioni hanno incluso pair programming per task complesse, con revisioni iterative che hanno affinato probabilità e logiche di gioco. Questo ha permesso adattamenti rapidi basati su feedback interni.

### Commenti sprint

Sprint dinamico che ha arricchito le meccaniche di gioco, con successi nel handling di accuracy. Suggerisce un maggiore focus su task aperte per chiudere loop di sviluppo.

## functional refactor

### Descrizione dell'andamento dello sviluppo
Sprint dedicato al refactoring funzionale avanzato, con enfasi su decisioni, validazioni e documentazione. Tutte le task sono state completate, inclusi refactoring di decision e act, con tempi da 2 a 20 ore. Ha mirato a ottimizzare l'architettura MVU e i layer decide-validate-act, raggiungendo una maturità funzionale elevata attraverso l'uso di monadi, DSL e pattern avanzati.

### Backlog
Il backlog includeva refactoring e docs:
- Refactor model e implement GUI (completato in 6 ore da Brini Tommaso).
- Implement ball player decision (movement) (completato in 20 ore da Brini Tommaso).
- Refactor decision (completato in 12 ore da Brini Tommaso).
- Docs su decide e strategy pattern (completato in 3 ore da Samite Mounir).
- Refactor act (completato in 12 ore da Rattini Emiliano).
- Fix validation probabilities (completato in 5 ore da Samite Mounir).
- Docs su act (completato in 3 ore da Rattini Emiliano).
- Docs su model (completato in 3 ore da Brini Tommaso).
- add score (completato in 8 ore da Samite Mounir).
- Refactor Team East e West (completato in 5 ore da Samite Mounir).
- creation dsl e refactor creazione situazioni di gioco (completato in 20 ore da Samite Mounir).
- Refactor test decision with dsl (completato in 5 ore da Brini Tommaso).
- Refactor test Act with dsl (completato in 5 ore da Rattini Emiliano).

### Iterazioni

Le iterazioni hanno coinvolto refactoring progressivi, con focus su programmazione funzionale avanzata. Aggiornamenti hanno tracciato avanzamenti in layer specifici, identificando e risolvendo fix per probabilità, culminando in un completamento integrale senza task residue.

### Commenti sprint

Sprint cruciale per la maturità funzionale, con enfasi su immutabilità e pattern. Il completamento di tutte le task ha assicurato stabilità complessiva.

## Commenti finali
Attraverso un approccio Scrum strutturato, il team ha dimostrato capacità di collaborazione, innovazione e attenzione alla qualità del codice. 
Gli sprint hanno evidenziato un'evoluzione progressiva, dal setup iniziale alle ottimizzazioni avanzate, con un totale di circa 220 ore distribuite in maniera equilibrata tra i membri del team che ha portato a un prodotto robusto e manutenibile.





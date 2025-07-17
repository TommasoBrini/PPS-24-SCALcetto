---
title: Specifica dei requisiti
nav_order: 3
parent: Report
---
# Specifica dei requisiti
## Requisiti di business
Il sistema SCALcetto ha come obiettivo la simulazione automatica di partite di calcetto, modellando le dinamiche di gioco secondo regole predefinite e comportamenti programmati dei giocatori.
I principali requisiti di business sono:
- **Simulazione realistica** -> il sistema deve permettere alla simulazione di partite di calcetto, riproducendo le dinamiche di gioco, le regole specificate e i comportamenti tipici dei giocatori
- **Regole** -> per ridurre la dimensione del problema e le casistiche di gioco, le regole di SCALcetto possono essere modificate rispetto alle regole del calcetto classico, per esempio:
    - No falli
    - La palla non esce dal campo, deve rimbalzare coerentemente
- **Sistema di giocatori intelligenti** -> i giocatori devono poter prendere decisioni in base allo stato della simulazione, scegliendo l'azione migliore da compiere.    
- **Modularità e manutenibilità**:
Il sistema deve essere progettato in modo modulare, per facilitare eventuali estensioni o modifiche future alle regole o ai comportamenti dei giocatori.

## Modello di dominio
Di seguito sono descritte le principali entità:
- MatchState: rappresenta lo stato della partita. Contiene le due squadre, il punteggio e la palla.
- Team: insieme di giocatori che difende un lato del campo (Side East o West). Contiene lista dei giocatori, side e un riferimento all'eventuale possesso palla.
- Player: giocatore composto da posizione, movimento, decisione in corso e relativa azione da compiere
- Ball: oggetto centrale della simulazione, contiene la posizione sul campo e del movimento attuale
- Decisioni e Azioni: insieme delle scelte che ogni giocatore può compiere in un determinato istante di tempo

## Requisiti Funzionali
### Requisiti Utente
Poiché questa versione del sistema non prevede interazione diretta con l’utente finale, i requisiti utente sono limitati alle funzionalità di avvio e osservazione della simulazione.
- L’utente deve poter avviare una simulazione
- L’utente deve poter osservare l’evoluzione della simulazione, con il rispettivo risultato della partita ben visibile
- L'utente deve poter stoppare la partita, farla ripartire oppure inizializzarla nuovamente

### Requisiti di Sistema
- Il sistema deve gestire la simulazione automatica della partita, aggiornando lo stato a ogni step temporale
- Il sistema deve modellare e aggiornare le posizioni, i movimenti e le azioni di tutti i giocatori e della palla
- Il sistema deve gestire correttamente i cambiamenti di stato, per esempio il cambio di possesso palla tra squadre.
- Il sistema deve applicare la logica decisionale dei giocatori e traducendo le intenzioni in azioni concrete
- Il sistema deve mantenere e aggiornare il punteggio della partita

## Requisiti non funzionali
Di seguito, i requisiti non funzionali e i rispettivi criteri di misurazione:
- **Affidabilità**: il sistema non deve generare eccezioni non gestite durante l'esecuzione di una simulazione standard. *Misurazione*: Esecuzione di almeno 10 simulazioni consecutive senza errori critici
- **Manutenibilità**: il codice deve rispettare le regole di clean code e deve essere correttamente documentato. *Misurazione*: presenza di una buona *scaladoc* per ogni API pubblica
- **Estendibilità**: Il progetto deve favorire la personalizzazione e l'aggiunta di nuove funzionalità. *Misurazione*: per aggiungere una nuova versione di una feature già implementata, non è stato necessario modificare più di 4 file.
- **Testabilità**: La copertura dei test automatici (unitari e di integrazione) deve essere almeno del 70%. *Misurazione*: Report di coverage generato da strumenti come scoverage o simili.
- **Portabilità**: Il sistema deve essere eseguibile su più sistemi operativi. *Misurazione*: esecuzione dei test su più sistemi operativi grazie alla continuous integration.






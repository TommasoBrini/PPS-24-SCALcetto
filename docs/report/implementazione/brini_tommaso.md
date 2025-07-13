---
title: Brini Tommaso
nav_order: 1
parent: Implementazione
---
# Brini Tommaso
Il mio contributo al progetto si concentra sulla fase di **decisione** all'interno del ciclo *decide-validate-act*, implementando un sistema di decision-making intelligente e modulare per i giocatori. L'architettura segue i principi di clean code e programmazione funzionale, garantendo il più possibile separazione delle responsabilità e dei componenti.

## Panoramica
Il sistema di decisione è strutturato in diversi livelli di astrazione
- **Decide** => Orchestratore principale che coordina l'intero processo decisionale per tutti i giocatori. E' l'entry point per la fase di decisione.
- **PlayerRoleFactory** e **PlayerTypes** => Sistema di mixin che definisce i ruoli dei giocatori con capacità decisionali specifiche.
- **DecisionMaker** => nucleo del sistema che in base al tipo del giocatore specifico richiama la giusta logica di selezione delle decisioni ottimali
- **Decisions** => Trait che definiscono le capacità decisionali specifiche //scrivi meglio
- **Behavior** => Moduli specializzati che valutano lo stato del match e guidano la selezione della decisione.
- **BallCarrierDecisionRating** => Sistema di rating che assegna punteggi alle decisioni del giocatore in possesso palla basandosi su criteri strategici e uniformi 

//qui puoi scrivere meglio
All'interno del Model, le possibili decisioni sono state implementate come una enum che rappresenta tutte le azioni disponibili per i giocatori:
<figure class="w-5 mx-auto">
  <img src="../assets/images/decision/decision.png" alt="Descriptive alt text">
  <figcaption>Possible Decision</figcaption>
</figure>

## Principi di Design 
- **Separazione delle Responsabilità** -> ogni componente ha una responsabilità ben definita

Di seguito verranno spiegati nel dettaglio le principali componenti

## Decide

## PlayerTypes

## Decisions

## Behavior

## BallCarrierDecisionRating

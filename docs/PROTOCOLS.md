# Comparaison des Protocoles

## S-MAC (Sensor-MAC)
**Principe** : Basé sur le cycle Écoute/Sommeil pour économiser l'énergie.
- **Cycle** : Les nœuds dorment 90% du temps et écoutent 10%.
- **Accès au canal** : Utilise le CSMA (Carrier Sense Multiple Access). Avant de transmettre, le nœud vérifie si le canal est libre.
- **Avantages** : Simple, s'adapte bien aux changements de topologie.
- **Inconvénients** : Collisions possibles, latence élevée due aux cycles de sommeil.

## L-MAC (Lightweight MAC)
**Principe** : Basé sur le TDMA (Time Division Multiple Access).
- **Slots** : Le temps est divisé en intervalles (slots). Chaque nœud possède son propre slot de transmission.
- **Accès au canal** : Garanti sans collision (dans cette implémentation simplifiée). Un nœud ne transmet que durant son slot.
- **Avantages** : Pas de collisions, consommation d'énergie très prévisible.
- **Inconvénients** : Moins flexible si le nombre de nœuds change, gaspillage de bande passante si un nœud n'a rien à envoyer durant son slot.

## Paramètres de Base
Les paramètres de base utilisés pour les protocoles sont les suivants :

- **S-MAC** : la période de cycle est de 100 ticks, avec un rapport veille/activité de 10 %, une fenêtre de contention de 31 et un intervalle de synchronisation de 1000.
- **CSMA** : la taille minimale et maximale de la fenêtre de contention sont respectivement 16 et 1024, avec un temps de backoff de 2 ticks et un mécanisme d’écoute du canal avant transmission.
- **L-MAC** : la trame est divisée en 32 slots, chaque nœud sélectionnant un slot de transmission unique, avec une durée de slot de 15 ms et une période de trame de 480 ms.

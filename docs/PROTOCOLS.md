# Comparaison des Protocoles

## S-MAC (Sensor-MAC)
**Principe** : Basé sur le cycle Écoute/Sommeil pour économiser l'énergie.
- **Cycle** : Les nœuds dorment 80% du temps et écoutent 20%.
- **Accès au canal** : Utilise le CSMA (Carrier Sense Multiple Access). Avant de transmettre, le nœud vérifie si le canal est libre.
- **Avantages** : Simple, s'adapte bien aux changements de topologie.
- **Inconvénients** : Collisions possibles, latence élevée due aux cycles de sommeil.

## L-MAC (Lightweight MAC)
**Principe** : Basé sur le TDMA (Time Division Multiple Access).
- **Slots** : Le temps est divisé en intervalles (slots). Chaque nœud possède son propre slot de transmission.
- **Accès au canal** : Garanti sans collision (dans cette implémentation simplifiée). Un nœud ne transmet que durant son slot.
- **Avantages** : Pas de collisions, consommation d'énergie très prévisible.
- **Inconvénients** : Moins flexible si le nombre de nœuds change, gaspillage de bande passante si un nœud n'a rien à envoyer durant son slot.

# Documentation du Simulateur WSN MAC

Ce projet est un simulateur de protocoles de couche MAC (Medium Access Control) pour les réseaux de capteurs sans fil (WSN). Il permet de comparer visuellement et statistiquement deux approches majeures : **S-MAC** (basé sur la contention/sommeil) et **L-MAC** (basé sur le TDMA).

## Structure du Projet

Le projet suit une architecture modulaire :
- `model/` : Représente les entités physiques (Nœuds, Paquets, Réseau).
- `protocol/` : Contient l'abstraction des protocoles et les implémentations SMAC/LMAC.
- `simulation/` : Le moteur de temps discret qui cadence la simulation.
- `ui/` : L'interface graphique JavaFX.
- `stats/` : Collecte des données de performance.

## Configuration de la simulation

Ce simulateur est une **implémentation Java personnalisée** développée pour l'étude des protocoles MAC. Les paramètres suivants définissent l'environnement de base :

### Environnement et Topologie
- **Topologie** : Distribution spatiale aléatoire (2D Random Distribution).
- **Dimensions** : 800 x 600 unités.
- **Nombre de nœuds** : Configurable de 1 à 100 (Défaut : 20).
- **Portée radio** : 150 unités.

### Modèle de Trafic
- **Type** : Génération aléatoire entre voisins.
- **Taux de génération** : 0.05 paquets/tick (probabilité système).
- **Durée de transmission** : 10 ticks (taille de paquet fixe).

### Paramètres Radio et Énergétiques
- **Énergie initiale** : 10 000 J (unités simulées).
- **Coût TX** : 1.5 J/tick.
- **Coût RX** : 1.0 J/tick.
- **Coût Idle** : 0.5 J/tick.
- **Coût Sleep** : 0.01 J/tick.

### Paramètres des Protocoles
- **S-MAC** : 
    - Cycle total : 100 ticks.
    - Phase active (Listen) : 20 ticks (coût de synchronisation/écoute).
    - Phase sommeil : 80 ticks.
- **CSMA** : Intégré à S-MAC via le *Carrier Sense* (vérification du canal libre avant envoi).
- **L-MAC** : 
    - Durée de slot : 15 ticks.
    - Frame : 1 slot par nœud (TDMA déterministe).

## Comment lancer le projet
Si Maven est installé :
1. `mvn clean compile`
2. `mvn javafx:run`

Sinon, importez le projet dans un IDE (IntelliJ/Eclipse) en tant que projet Maven.

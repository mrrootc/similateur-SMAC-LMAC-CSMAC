# Architecture Technique

Le simulateur repose sur un moteur à **temps discret** (Discrete-Time Simulation).

## 1. Le Moteur de Simulation (`SimulationEngine`)
À chaque "tick" (itération), le moteur effectue les étapes suivantes :
1. **Génération de trafic** : Probabilité aléatoire qu'un nœud génère un nouveau paquet pour un voisin.
2. **Logique Protocolaire** : Chaque nœud demande au protocole sélectionné quelle doit être sa prochaine action (Dormir, Écouter, Transmettre).
3. **Résolution physique** : Le moteur vérifie les transmissions en cours, gère les collisions et livre les paquets aux destinations.
4. **Modèle Énergétique** : Calcul de la consommation basée sur l'état du nœud.

## 2. Modèle de Communication
- **Portée** : Fixée à 150 unités. Uniquement les nœuds dans cette zone peuvent communiquer.
- **Collisions** : Si un nœud reçoit deux signaux en même temps, le paquet est marqué comme corrompu (perdu).
- **Débit** : Les paquets ont une durée de transmission fixe (10 ticks).

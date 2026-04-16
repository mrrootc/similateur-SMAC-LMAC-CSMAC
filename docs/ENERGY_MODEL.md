# Modèle Énergétique

La gestion de l'énergie est cruciale dans les réseaux de capteurs. Le simulateur utilise les paramètres suivants :

| État | Coût (unités/tick) | Description |
| :--- | :--- | :--- |
| **TRANSMIT** | 1.5 | Coût le plus élevé (amplification du signal). |
| **RECEIVE** | 1.0 | Coût élevé (décodage du signal). |
| **IDLE** | 0.5 | Écoute du canal (Idle Listening). |
| **SLEEP** | 0.01 | Consommation quasi-nulle. |

## Calcul de la durée de vie
Chaque nœud commence avec **10 000 unités**.
- Un nœud est déclaré "Mort" (Dead) quand son énergie tombe à 0.
- Il devient alors gris sur l'interface et ne peut plus participer au réseau.
- Les statistiques affichent la consommation totale cumulée pour comparer l'efficacité des protocoles.

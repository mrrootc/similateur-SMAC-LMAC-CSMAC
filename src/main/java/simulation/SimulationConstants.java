package simulation;

public class SimulationConstants {
    // Paramètres Énergétiques (J/tick)
    public static final double ENERGY_TX = 1.5;
    public static final double ENERGY_RX = 1.0;
    public static final double ENERGY_IDLE = 0.5;
    public static final double ENERGY_SLEEP = 0.01;
    public static final double INITIAL_ENERGY = 10000.0;

    // Paramètres Radio et Réseau
    public static final double COMM_RANGE = 150.0;
    public static final int PACKET_DURATION = 10; // Ticks
    
    // Modèle de Trafic
    public static final double TRAFFIC_GENERATION_RATE = 0.05; // Probabilité par tick

    // Paramètres S-MAC
    public static final int SMAC_LISTEN_TIME = 20;
    public static final int SMAC_SLEEP_TIME = 80;
    
    // Paramètres L-MAC
    public static final int LMAC_SLOT_EXTRA_TIME = 5;
    public static final int LMAC_SLOT_DURATION = PACKET_DURATION + LMAC_SLOT_EXTRA_TIME;
}

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
    public static final int SMAC_LISTEN_TIME = 10; // 10% duty cycle
    public static final int SMAC_SLEEP_TIME = 90; // Pour un cycle de 100 ticks
    public static final int SMAC_CONTENTION_WINDOW = 31;
    public static final int SMAC_SYNC_INTERVAL = 1000;
    
    // Paramètres CSMA
    public static final int CSMA_MIN_CONTENTION_WINDOW = 16;
    public static final int CSMA_MAX_CONTENTION_WINDOW = 1024;
    public static final int CSMA_BACKOFF_TIME = 2; // Ticks

    // Paramètres L-MAC
    public static final int LMAC_SLOTS_PER_FRAME = 32;
    public static final int LMAC_SLOT_EXTRA_TIME = 5;
    public static final int LMAC_SLOT_DURATION = PACKET_DURATION + LMAC_SLOT_EXTRA_TIME; // 15 ms
    public static final int LMAC_FRAME_DURATION = LMAC_SLOTS_PER_FRAME * LMAC_SLOT_DURATION; // 480 ms
}

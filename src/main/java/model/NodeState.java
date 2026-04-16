package model;

public enum NodeState {
    SLEEP,      // Low power, cannot transmit or receive
    IDLE,       // Listening to the channel
    TRANSMIT,   // Actively sending data
    RECEIVE     // Actively receiving data
}

package model;

import simulation.SimulationConstants;
import java.util.LinkedList;
import java.util.Queue;

public class Node {
    private final int id;
    private final double x;
    private final double y;
    
    private double energyLevel;
    private NodeState state;
    
    private final Queue<Packet> transmitQueue;
    private Packet currentReceivingPacket;
    
    private int protocolTimer = 0;
    private int assignedSlot = -1;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energyLevel = SimulationConstants.INITIAL_ENERGY;
        this.state = NodeState.IDLE;
        this.transmitQueue = new LinkedList<>();
    }

    public void queuePacket(Packet p) {
        transmitQueue.offer(p);
    }
    
    public Packet peekNextPacket() { return transmitQueue.peek(); }
    public Packet dequeuePacket() { return transmitQueue.poll(); }
    public int getPacketQueueSize() { return transmitQueue.size(); }
    public boolean hasPacketsToSend() { return !transmitQueue.isEmpty(); }

    public double distanceTo(Node other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getEnergyLevel() { return energyLevel; }
    public NodeState getState() { return state; }
    
    public void setState(NodeState state) { this.state = state; }
    public void consumeEnergy(double amount) { this.energyLevel -= amount; }
    
    public int getProtocolTimer() { return protocolTimer; }
    public void setProtocolTimer(int timer) { this.protocolTimer = timer; }
    public void decrementProtocolTimer() { if (protocolTimer > 0) protocolTimer--; }

    public int getAssignedSlot() { return assignedSlot; }
    public void setAssignedSlot(int slot) { this.assignedSlot = slot; }

    public void setReceivingPacket(Packet p) { this.currentReceivingPacket = p; }
    public Packet getReceivingPacket() { return currentReceivingPacket; }
    
    public boolean isDead() { return energyLevel <= 0; }
}

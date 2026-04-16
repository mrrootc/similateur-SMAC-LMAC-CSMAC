package stats;

public class Statistics {
    private long totalPacketsSent;
    private long totalPacketsReceived;
    private long totalCollisions;
    private double totalEnergyConsumed;

    public void reset() {
        totalPacketsSent = 0;
        totalPacketsReceived = 0;
        totalCollisions = 0;
        totalEnergyConsumed = 0;
    }

    public void recordPacketSent() { totalPacketsSent++; }
    public void recordPacketReceived() { totalPacketsReceived++; }
    public void recordCollision() { totalCollisions++; }
    public void addEnergyConsumed(double energy) { totalEnergyConsumed += energy; }

    public long getPacketsSent() { return totalPacketsSent; }
    public long getPacketsReceived() { return totalPacketsReceived; }
    public long getCollisions() { return totalCollisions; }
    public double getTotalEnergyConsumed() { return totalEnergyConsumed; }
    
    public double getPacketDeliveryRatio() {
        if (totalPacketsSent == 0) return 0;
        return (double) totalPacketsReceived / totalPacketsSent * 100.0;
    }
}

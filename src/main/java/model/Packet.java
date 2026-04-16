package model;

public class Packet {
    private final Node source;
    private final Node destination;
    private final int size;
    private final long timestamp;

    public Packet(Node source, Node destination, int size, long timestamp) {
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.timestamp = timestamp;
    }

    public Node getSource() { return source; }
    public Node getDestination() { return destination; }
    public int getSize() { return size; }
    public long getTimestamp() { return timestamp; }
}

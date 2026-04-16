package simulation;

import model.Network;
import model.Node;
import model.NodeState;
import model.Packet;
import protocol.MacProtocol;
import stats.Statistics;

import java.util.List;
import java.util.Random;

public class SimulationEngine {
    private Network network;
    private MacProtocol protocol;
    private Statistics stats;
    
    private long currentTime;
    private boolean isRunning;
    private Random random;
    private java.util.List<String> eventLog = new java.util.ArrayList<>();

    public SimulationEngine() {
        this.stats = new Statistics();
        this.currentTime = 0;
        this.isRunning = false;
        this.random = new Random();
    }

    public void logEvent(String message) {
        eventLog.add(String.format("[%d] %s", currentTime, message));
        if (eventLog.size() > 50) eventLog.remove(0);
    }

    public java.util.List<String> getEventLog() {
        return new java.util.ArrayList<>(eventLog);
    }

    public void setup(int numNodes, double width, double height, MacProtocol protocol) {
        this.network = new Network(numNodes, width, height);
        this.protocol = protocol;
        this.stats.reset();
        this.currentTime = 0;
        this.eventLog.clear();
        this.protocol.initialize(this.network, this.stats);
        logEvent("Network setup with " + numNodes + " nodes using " + protocol.getName());
    }

    public void addNode(double x, double y) {
        if (network == null) return;
        network.addNode(x, y);
        Node newNode = network.getNodes().get(network.getNodes().size() - 1);
        if (protocol != null) {
            protocol.onNodeAdded(newNode);
        }
        logEvent("Added Node " + newNode.getId() + " at (" + (int)x + "," + (int)y + ")");
    }

    public void clearNetwork() {
        isRunning = false;
        if (network != null) {
            network.clearNodes();
        }
        if (protocol != null) {
            protocol.reset();
        }
        stats.reset();
        currentTime = 0;
        eventLog.clear();
        logEvent("Network cleared");
    }

    public void start() { 
        isRunning = true; 
        logEvent("Simulation started");
    }
    public void pause() { 
        isRunning = false; 
        logEvent("Simulation paused");
    }

    public void tick() {
        if (!isRunning) return;

        generateRandomTraffic();

        for (Node node : network.getNodes()) {
            protocol.executeStep(node, currentTime);
        }

        protocol.handleTransmissions(currentTime);

        updateEnergyModel();

        currentTime++;
    }

    private void generateRandomTraffic() {
        List<Node> nodes = network.getNodes();
        if (nodes.isEmpty()) return;

        if (random.nextDouble() < SimulationConstants.TRAFFIC_GENERATION_RATE) { 
            Node src = nodes.get(random.nextInt(nodes.size()));
            if (!src.isDead()) {
                List<Node> neighbors = network.getNeighbors(src);
                if (!neighbors.isEmpty()) {
                    Node dest = neighbors.get(random.nextInt(neighbors.size()));
                    src.queuePacket(new Packet(src, dest, 512, currentTime));
                    logEvent("Node " + src.getId() + " generated packet for Node " + dest.getId());
                }
            }
        }
    }

    private void updateEnergyModel() {
        for (Node node : network.getNodes()) {
            if (node.isDead()) continue;

            double cost = 0;
            switch (node.getState()) {
                case TRANSMIT: cost = SimulationConstants.ENERGY_TX; break;
                case RECEIVE: cost = SimulationConstants.ENERGY_RX; break;
                case IDLE: cost = SimulationConstants.ENERGY_IDLE; break;
                case SLEEP: cost = SimulationConstants.ENERGY_SLEEP; break;
            }
            
            node.consumeEnergy(cost);
            stats.addEnergyConsumed(cost);
            
            if (node.isDead()) {
                node.setState(NodeState.SLEEP);
            }
        }
    }

    public Network getNetwork() { return network; }
    public Statistics getStats() { return stats; }
    public long getCurrentTime() { return currentTime; }
}

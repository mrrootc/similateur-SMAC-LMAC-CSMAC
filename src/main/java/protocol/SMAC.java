package protocol;

import model.Node;
import model.NodeState;
import model.Packet;
import simulation.SimulationConstants;
import java.util.List;

public class SMAC extends MacProtocol {
    private static final int LISTEN_TIME = SimulationConstants.SMAC_LISTEN_TIME;
    private static final int SLEEP_TIME = SimulationConstants.SMAC_SLEEP_TIME;
    private static final int CYCLE_TIME = LISTEN_TIME + SLEEP_TIME;
    private static final int SYNC_INTERVAL = SimulationConstants.SMAC_SYNC_INTERVAL;
    private static final int CONTENTION_WINDOW = SimulationConstants.SMAC_CONTENTION_WINDOW;

    @Override
    public String getName() { return "S-MAC"; }

    @Override
    protected void setupNodes() {
        for (Node node : network.getNodes()) {
            node.setProtocolTimer((int) (Math.random() * CYCLE_TIME));
        }
    }

    @Override
    public void onNodeAdded(Node node) {
        node.setProtocolTimer((int) (Math.random() * CYCLE_TIME));
    }

    @Override
    public void reset() {
        // Pas d'état global à réinitialiser pour S-MAC
    }

    @Override
    public String getCurrentStatus(long currentTime) {
        int timeInCycle = (int) (currentTime % CYCLE_TIME);
        if (timeInCycle < LISTEN_TIME) {
            return "S-MAC Phase: LISTEN (Active)";
        } else {
            return "S-MAC Phase: SLEEP (Energy Saving)";
        }
    }

    @Override
    public void executeStep(Node node, long currentTime) {
        if (node.isDead()) {
            node.setState(NodeState.SLEEP);
            return;
        }

        if (node.getState() == NodeState.TRANSMIT || node.getState() == NodeState.RECEIVE) {
            node.decrementProtocolTimer();
            if (node.getProtocolTimer() <= 0) {
                node.setState(NodeState.IDLE);
                node.setReceivingPacket(null);
            }
            return;
        }

        int timeInCycle = (int) (currentTime % CYCLE_TIME);
        
        if (timeInCycle < LISTEN_TIME) {
            node.setState(NodeState.IDLE);
            
            if (node.hasPacketsToSend()) {
                if (node.getContentionWindow() == 0) {
                    node.setContentionWindow(CONTENTION_WINDOW);
                    node.setBackoffCounter((int) (Math.random() * CONTENTION_WINDOW));
                }

                if (isChannelClear(node)) {
                    if (node.getBackoffCounter() > 0) {
                        node.decrementBackoff();
                    } else {
                        node.setState(NodeState.TRANSMIT);
                        node.setProtocolTimer(SimulationConstants.PACKET_DURATION);
                        stats.recordPacketSent();
                        node.setContentionWindow(0);
                    }
                }
            } else {
                node.setContentionWindow(0);
            }
        } else {
            if (node.hasPacketsToSend()) {
                node.setState(NodeState.IDLE);
            } else {
                node.setState(NodeState.SLEEP);
            }
        }
    }

    private boolean isChannelClear(Node node) {
        for (Node neighbor : network.getNeighbors(node)) {
            if (neighbor.getState() == NodeState.TRANSMIT) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void handleTransmissions(long currentTime) {
        for (Node node : network.getNodes()) {
            if (node.getState() == NodeState.TRANSMIT && node.getProtocolTimer() == SimulationConstants.PACKET_DURATION) {
                Packet p = node.peekNextPacket();
                if (p == null) continue;

                List<Node> neighbors = network.getNeighbors(node);
                for (Node neighbor : neighbors) {
                    if (neighbor.isDead() || neighbor.getState() == NodeState.SLEEP) continue;

                    if (neighbor.getState() == NodeState.RECEIVE) {
                        stats.recordCollision();
                        neighbor.setReceivingPacket(null);
                    } else if (neighbor.getState() == NodeState.IDLE) {
                        neighbor.setState(NodeState.RECEIVE);
                        neighbor.setProtocolTimer(SimulationConstants.PACKET_DURATION);
                        neighbor.setReceivingPacket(p);
                        if (p.getDestination() == neighbor) {
                            stats.recordPacketReceived();
                        }
                    }
                }
                node.dequeuePacket();
            }
        }
    }
}

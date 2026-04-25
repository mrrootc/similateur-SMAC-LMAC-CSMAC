package protocol;

import model.Node;
import model.NodeState;
import model.Packet;
import simulation.SimulationConstants;
import java.util.List;

public class LMAC extends MacProtocol {
    private static final int SLOT_DURATION = SimulationConstants.LMAC_SLOT_DURATION;
    private static final int TOTAL_SLOTS = SimulationConstants.LMAC_SLOTS_PER_FRAME;

    @Override
    public String getName() { return "L-MAC"; }

    @Override
    protected void setupNodes() {
        for (int i = 0; i < network.getNodes().size(); i++) {
            Node node = network.getNodes().get(i);
            node.setAssignedSlot(i % TOTAL_SLOTS);
        }
    }

    @Override
    public void onNodeAdded(Node node) {
        node.setAssignedSlot((network.getNodes().size() - 1) % TOTAL_SLOTS);
    }

    @Override
    public void reset() {
    }

    @Override
    public String getCurrentStatus(long currentTime) {
        if (network.getNodes().isEmpty()) return "L-MAC: No Nodes";
        long frameTime = currentTime % (TOTAL_SLOTS * (long)SLOT_DURATION);
        int currentSlot = (int) (frameTime / SLOT_DURATION);
        return String.format("L-MAC Frame: Slot %d/%d is active", currentSlot, TOTAL_SLOTS);
    }

    @Override
    public void executeStep(Node node, long currentTime) {
        if (node.isDead() || network.getNodes().isEmpty()) {
            node.setState(NodeState.SLEEP);
            return;
        }

        if (node.getState() == NodeState.TRANSMIT || node.getState() == NodeState.RECEIVE) {
            node.decrementProtocolTimer();
            if (node.getProtocolTimer() <= 0) {
                node.setState(NodeState.SLEEP);
                node.setReceivingPacket(null);
            }
            return;
        }

        long frameTime = currentTime % (TOTAL_SLOTS * (long)SLOT_DURATION);
        int currentSlot = (int) (frameTime / SLOT_DURATION);

        if (currentSlot == node.getAssignedSlot()) {
            if (node.hasPacketsToSend()) {
                node.setState(NodeState.TRANSMIT);
                node.setProtocolTimer(SimulationConstants.PACKET_DURATION);
                stats.recordPacketSent();
            } else {
                node.setState(NodeState.SLEEP);
            }
        } else {
            int timeInSlot = (int) (frameTime % SLOT_DURATION);
            if (timeInSlot == 0) {
                node.setState(NodeState.IDLE);
            } else if (node.getState() == NodeState.IDLE) {
                 node.setState(NodeState.SLEEP);
            }
        }
    }

    @Override
    public void handleTransmissions(long currentTime) {
        for (Node node : network.getNodes()) {
            if (node.getState() == NodeState.TRANSMIT && node.getProtocolTimer() == SimulationConstants.PACKET_DURATION) {
                Packet p = node.peekNextPacket();
                if (p == null) continue;

                for (Node neighbor : network.getNeighbors(node)) {
                    if (neighbor.isDead()) continue;
                    if (p.getDestination() == neighbor) {
                        neighbor.setState(NodeState.RECEIVE);
                        neighbor.setProtocolTimer(SimulationConstants.PACKET_DURATION);
                        neighbor.setReceivingPacket(p);
                        stats.recordPacketReceived();
                    }
                }
                node.dequeuePacket();
            }
        }
    }
}

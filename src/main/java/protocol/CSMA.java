package protocol;

import model.Node;
import model.NodeState;
import model.Packet;
import simulation.SimulationConstants;
import java.util.List;

/**
 * Implémentation de base du protocole CSMA (Carrier Sense Multiple Access).
 * Les nœuds sont toujours à l'écoute (pas de cycle de sommeil contrairement à S-MAC).
 */
public class CSMA extends MacProtocol {

    @Override
    public String getName() { return "CSMA"; }

    @Override
    protected void setupNodes() {
        // Pas de configuration spécifique nécessaire
    }

    @Override
    public void onNodeAdded(Node node) {
        // Pas d'action nécessaire
    }

    @Override
    public void reset() {
        // Pas d'état global
    }

    @Override
    public String getCurrentStatus(long currentTime) {
        return "CSMA: Active (Always Listening)";
    }

    @Override
    public void executeStep(Node node, long currentTime) {
        if (node.isDead()) {
            node.setState(NodeState.SLEEP);
            return;
        }

        // Si le nœud est déjà en train de transmettre ou recevoir, on continue
        if (node.getState() == NodeState.TRANSMIT || node.getState() == NodeState.RECEIVE) {
            node.decrementProtocolTimer();
            if (node.getProtocolTimer() <= 0) {
                node.setState(NodeState.IDLE);
                node.setReceivingPacket(null);
            }
            return;
        }

        // Par défaut, en CSMA pur sans sommeil, le nœud est en IDLE (écoute)
        node.setState(NodeState.IDLE);

        // Si on a des paquets à envoyer
        if (node.hasPacketsToSend()) {
            if (node.getContentionWindow() == 0) {
                // Initialize contention window and backoff
                node.setContentionWindow(SimulationConstants.CSMA_MIN_CONTENTION_WINDOW);
                node.setBackoffCounter((int) (Math.random() * node.getContentionWindow()) * SimulationConstants.CSMA_BACKOFF_TIME);
            }

            if (isChannelClear(node)) {
                if (node.getBackoffCounter() > 0) {
                    node.decrementBackoff();
                } else {
                    // Transmission
                    node.setState(NodeState.TRANSMIT);
                    node.setProtocolTimer(SimulationConstants.PACKET_DURATION);
                    stats.recordPacketSent();
                    node.setContentionWindow(0); // Reset CW after successful transmission
                }
            } else {
                // Channel busy, freeze or double backoff/CW
                // For this implementation, wait until channel is clear to resume backoff.
                // Optionally double CW here for collisions, handled in handleTransmissions.
            }
        } else {
            node.setContentionWindow(0);
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
                    if (neighbor.isDead()) continue;

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

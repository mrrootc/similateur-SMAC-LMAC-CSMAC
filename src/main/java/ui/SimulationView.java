package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Network;
import model.Node;
import model.NodeState;
import simulation.SimulationConstants;

public class SimulationView extends Canvas {
    
    private Node selectedNode;
    private String statusText = "";

    public SimulationView(double width, double height) {
        super(width, height);
    }

    public void setStatusText(String text) {
        this.statusText = text;
    }

    public void setSelectedNode(Node node) {
        this.selectedNode = node;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public Node findNodeAt(double x, double y, Network network) {
        if (network == null) return null;
        double radius = 10.0;
        for (Node node : network.getNodes()) {
            double dx = node.getX() - x;
            double dy = node.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) <= radius) {
                return node;
            }
        }
        return null;
    }

    public void draw(Network network) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.web("#2b2b2b"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Draw Protocol Status
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("System", 16));
        gc.fillText(statusText, 20, 30);

        if (network == null) return;

        // Draw selection range
        if (selectedNode != null) {
            gc.setStroke(Color.web("#444444"));
            gc.setLineDashes(5);
            gc.strokeOval(selectedNode.getX() - SimulationConstants.COMM_RANGE, 
                          selectedNode.getY() - SimulationConstants.COMM_RANGE, 
                          SimulationConstants.COMM_RANGE * 2, 
                          SimulationConstants.COMM_RANGE * 2);
            gc.setLineDashes(null);
        }

        gc.setLineWidth(1.5);
        for (Node node : network.getNodes()) {
            if (node.getState() == NodeState.TRANSMIT) {
                gc.setStroke(Color.RED.deriveColor(1, 1, 1, 0.4));
                gc.strokeOval(node.getX() - SimulationConstants.COMM_RANGE, 
                              node.getY() - SimulationConstants.COMM_RANGE, 
                              SimulationConstants.COMM_RANGE * 2, 
                              SimulationConstants.COMM_RANGE * 2);

                for (Node neighbor : network.getNeighbors(node)) {
                    gc.strokeLine(node.getX(), node.getY(), neighbor.getX(), neighbor.getY());
                }
            }
        }

        double radius = 6.0;
        for (Node node : network.getNodes()) {
            if (node == selectedNode) {
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeOval(node.getX() - radius - 2, node.getY() - radius - 2, (radius + 2) * 2, (radius + 2) * 2);
            }

            if (node.isDead()) {
                gc.setFill(Color.DARKGRAY);
            } else {
                switch (node.getState()) {
                    case IDLE: gc.setFill(Color.LIMEGREEN); break;
                    case SLEEP: gc.setFill(Color.ROYALBLUE); break;
                    case TRANSMIT: gc.setFill(Color.RED); break;
                    case RECEIVE: gc.setFill(Color.YELLOW); break;
                }
            }
            gc.fillOval(node.getX() - radius, node.getY() - radius, radius * 2, radius * 2);
            
            if (!node.isDead()) {
                double energyPct = node.getEnergyLevel() / SimulationConstants.INITIAL_ENERGY;
                gc.setFill(Color.RED);
                gc.fillRect(node.getX() - 10, node.getY() + 10, 20, 3);
                gc.setFill(Color.LIMEGREEN);
                gc.fillRect(node.getX() - 10, node.getY() + 10, 20 * energyPct, 3);
            }
        }
    }
}

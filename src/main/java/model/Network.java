package model;

import simulation.SimulationConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Network {
    private final List<Node> nodes;
    private final double width;
    private final double height;

    public Network(int numNodes, double width, double height) {
        this.width = width;
        this.height = height;
        this.nodes = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numNodes; i++) {
            addNode(rand.nextDouble() * width, rand.nextDouble() * height);
        }
    }

    public void addNode(double x, double y) {
        nodes.add(new Node(nodes.size(), x, y));
    }

    public void clearNodes() {
        nodes.clear();
    }

    public List<Node> getNodes() { return nodes; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (Node n : nodes) {
            if (n != node && node.distanceTo(n) <= SimulationConstants.COMM_RANGE) {
                neighbors.add(n);
            }
        }
        return neighbors;
    }
}

package protocol;

import model.Network;
import model.Node;
import stats.Statistics;

public abstract class MacProtocol {
    protected Network network;
    protected Statistics stats;

    public void initialize(Network network, Statistics stats) {
        this.network = network;
        this.stats = stats;
        setupNodes();
    }

    protected abstract void setupNodes();
    public abstract void onNodeAdded(Node node);
    public abstract void reset();
    public abstract void executeStep(Node node, long currentTime);
    public abstract void handleTransmissions(long currentTime);
    public abstract String getName();
    public abstract String getCurrentStatus(long currentTime);
}

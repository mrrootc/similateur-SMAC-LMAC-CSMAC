package ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import protocol.LMAC;
import protocol.SMAC;
import protocol.CSMA;
import simulation.SimulationEngine;

public class MainApp extends Application {

    private SimulationEngine engine;
    private SimulationView simulationView;
    private Label statsLabel;
    private Label nodeDetailsLabel;
    private TextArea eventLogArea;
    private ComboBox<String> protocolCombo;
    private Slider nodeCountSlider;
    private Slider speedSlider;
    private ToggleButton btnAddNodeMode;

    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        engine = new SimulationEngine();
        
        BorderPane root = new BorderPane();
        simulationView = new SimulationView(CANVAS_WIDTH, CANVAS_HEIGHT);
        root.setCenter(simulationView);

        setupCanvasInteraction();

        VBox rightPanel = createControlPanel();
        root.setRight(rightPanel);

        // Ajout d'une zone pour les statistiques globales
        statsLabel = new Label();
        statsLabel.setStyle("-fx-font-family: 'Consolas', monospace; -fx-padding: 10; -fx-background-color: #eee;");
        root.setBottom(statsLabel);

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            private long tickAccumulator = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                long elapsedNano = now - lastUpdate;
                lastUpdate = now;
                tickAccumulator += elapsedNano;

                // Vitesse basée sur le slider (1 à 60 ticks par seconde)
                long targetNanosPerTick = (long) (1_000_000_000 / speedSlider.getValue());

                while (tickAccumulator >= targetNanosPerTick) {
                    engine.tick();
                    tickAccumulator -= targetNanosPerTick;
                }

                simulationView.draw(engine.getNetwork());
                // Afficher le statut du protocole sur le canvas
                if (engine.getNetwork() != null && engine.getNetwork().getNodes().size() > 0) {
                    String status = ((protocol.MacProtocol)reflectGetProtocol(engine)).getCurrentStatus(engine.getCurrentTime());
                    simulationView.setStatusText(status);
                }
                
                updateStatsDisplay();
                updateNodeDetailsDisplay();
                updateEventLog();
            }
        };
        timer.start();

        Scene scene = new Scene(root);
        primaryStage.setTitle("WSN MAC Protocol Simulator (CSMA vs SMAC vs LMAC)");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        resetSimulation();
    }

    // Helper technique pour obtenir le protocole sans changer l'architecture
    private Object reflectGetProtocol(Object engine) {
        try {
            java.lang.reflect.Field f = engine.getClass().getDeclaredField("protocol");
            f.setAccessible(true);
            return f.get(engine);
        } catch (Exception e) { return null; }
    }

    private void updateEventLog() {
        java.util.List<String> logs = engine.getEventLog();
        StringBuilder sb = new StringBuilder();
        for (int i = logs.size() - 1; i >= 0; i--) {
            sb.append(logs.get(i)).append("\n");
        }
        eventLogArea.setText(sb.toString());
    }

    private void setupCanvasInteraction() {
        simulationView.setOnMouseClicked(e -> {
            if (btnAddNodeMode.isSelected()) {
                engine.addNode(e.getX(), e.getY());
            } else {
                model.Node node = simulationView.findNodeAt(e.getX(), e.getY(), engine.getNetwork());
                simulationView.setSelectedNode(node);
            }
        });
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(280);
        panel.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");

        Label titleLabel = new Label("Network Settings");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label typeLabel = new Label("Implémentation Java Personnalisée");
        typeLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666; -fx-font-size: 10px;");

        protocolCombo = new ComboBox<>();
        protocolCombo.getItems().addAll("CSMA", "S-MAC", "L-MAC");
        protocolCombo.setValue("S-MAC");
        protocolCombo.setMaxWidth(Double.MAX_VALUE);

        Label sliderLabel = new Label("Initial Nodes: 20");
        nodeCountSlider = new Slider(1, 100, 20);
        nodeCountSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            sliderLabel.setText(String.format("Initial Nodes: %d", newVal.intValue())));

        btnAddNodeMode = new ToggleButton("Add Node Mode");
        btnAddNodeMode.setMaxWidth(Double.MAX_VALUE);

        Button btnClear = new Button("Clear All Nodes");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setOnAction(e -> {
            engine.clearNetwork();
            simulationView.setSelectedNode(null);
            simulationView.draw(engine.getNetwork());
        });

        VBox setupBox = new VBox(5, new Label("Protocol:"), protocolCombo, sliderLabel, nodeCountSlider, btnAddNodeMode, btnClear);
        setupBox.setStyle("-fx-padding: 8; -fx-background-color: #e8e8e8; -fx-background-radius: 5;");

        Label controlTitle = new Label("Simulation Controls");
        controlTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label speedLabel = new Label("Speed: 10 ticks/sec");
        speedSlider = new Slider(1, 60, 10);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            speedLabel.setText(String.format("Speed: %d ticks/sec", newVal.intValue())));

        Button btnStart = new Button("Start");
        Button btnPause = new Button("Pause");
        Button btnReset = new Button("Reset");
        btnStart.setMaxWidth(Double.MAX_VALUE);
        btnPause.setMaxWidth(Double.MAX_VALUE);
        btnReset.setMaxWidth(Double.MAX_VALUE);

        btnStart.setOnAction(e -> engine.start());
        btnPause.setOnAction(e -> engine.pause());
        btnReset.setOnAction(e -> resetSimulation());

        HBox buttonBox = new HBox(5, btnStart, btnPause, btnReset);
        buttonBox.setAlignment(Pos.CENTER);

        VBox controlBox = new VBox(5, controlTitle, speedLabel, speedSlider, buttonBox);
        controlBox.setStyle("-fx-padding: 8; -fx-background-color: #e8e8e8; -fx-background-radius: 5;");

        Label logTitle = new Label("Recent Events");
        logTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        eventLogArea = new TextArea();
        eventLogArea.setEditable(false);
        eventLogArea.setPrefHeight(150);
        eventLogArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 10px;");

        Label nodeDetailsTitle = new Label("Selected Node Details");
        nodeDetailsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nodeDetailsLabel = new Label("No node selected");
        nodeDetailsLabel.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 11px;");

        panel.getChildren().addAll(
            titleLabel, typeLabel, setupBox,
            controlBox,
            new Separator(),
            logTitle, eventLogArea,
            new Separator(),
            nodeDetailsTitle, nodeDetailsLabel
        );

        return panel;
    }

    private void resetSimulation() {
        engine.pause();
        protocol.MacProtocol selectedProtocol;
        switch (protocolCombo.getValue()) {
            case "CSMA": selectedProtocol = new CSMA(); break;
            case "L-MAC": selectedProtocol = new LMAC(); break;
            default: selectedProtocol = new SMAC(); break;
        }
        engine.setup((int)nodeCountSlider.getValue(), CANVAS_WIDTH, CANVAS_HEIGHT, selectedProtocol);
        simulationView.setSelectedNode(null);
        simulationView.draw(engine.getNetwork());
        updateStatsDisplay();
    }

    private void updateStatsDisplay() {
        if (statsLabel == null) return;
        stats.Statistics stats = engine.getStats();
        String info = String.format(
            "Time: %d ticks | Energy: %.2f J | Packets: %d TX / %d RX | Collisions: %d | PDR: %.1f%% | Dead Nodes: %d",
            engine.getCurrentTime(),
            stats.getTotalEnergyConsumed(),
            stats.getPacketsSent(),
            stats.getPacketsReceived(),
            stats.getCollisions(),
            stats.getPacketDeliveryRatio(),
            engine.getNetwork() != null ? engine.getNetwork().getNodes().stream().filter(model.Node::isDead).count() : 0
        );
        statsLabel.setText(info);
    }

    private void updateNodeDetailsDisplay() {
        model.Node node = simulationView.getSelectedNode();
        if (node == null) {
            nodeDetailsLabel.setText("No node selected\nClick a node to view info");
            return;
        }

        String info = String.format(
            "Node ID: %d\n" +
            "State: %s\n" +
            "Energy: %.2f / 100\n" +
            "Queue: %d packets\n" +
            "Position: (%.0f, %.0f)",
            node.getId(),
            node.getState(),
            node.getEnergyLevel(),
            node.getPacketQueueSize(),
            node.getX(), node.getY()
        );
        nodeDetailsLabel.setText(info);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

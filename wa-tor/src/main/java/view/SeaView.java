package view;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Animal;
import model.Position;
import model.Sea;

public class SeaView extends BorderPane {
    private final Sea sea;
    private final Canvas canvas;
    private final Label turnLabel = new Label();
    private final Label tunaLabel = new Label();
    private final Label sharkLabel = new Label();
    private final Label emptyLabel = new Label();
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<Number, Number> graph;
    private final XYChart.Series<Number, Number> tunaSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> sharkSeries = new XYChart.Series<>();
    private boolean running = true;
    private long lastTick = 0;

    public SeaView(Sea sea) {
        this.sea = sea;
        this.canvas = new Canvas(sea.getWidth() * Sea.CELL_SIZE, sea.getHeight() * Sea.CELL_SIZE);

        xAxis.setLabel("Tour");
        yAxis.setLabel("Population");
        graph = new LineChart<>(xAxis, yAxis);
        graph.setMinWidth(320);
        graph.setCreateSymbols(false);
        graph.setLegendVisible(true);
        tunaSeries.setName("Thons");
        sharkSeries.setName("Requins");
        graph.getData().addAll(tunaSeries, sharkSeries);

        turnoutLabels();
        refresh();

        setCenter(canvas);
        turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        tunaLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #8ef0a3;");
        sharkLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff8a80;");
        emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #dfe8f3;");

        VBox rightPanel = new VBox(12, turnLabel, tunaLabel, sharkLabel, emptyLabel, graph);
        rightPanel.setStyle("-fx-padding: 14; -fx-background-color: #202832; -fx-border-color: #3a4d63; -fx-border-width: 0 0 0 2;");
        setRight(rightPanel);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running && now - lastTick >= 100_000_000L) {
                    advance();
                    lastTick = now;
                }
            }
        };
        timer.start();
    }

    public void advance() {
        sea.advanceTurn();
        refresh();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void attach(javafx.scene.Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                running = !running;
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                javafx.application.Platform.exit();
            }
        });
    }

    private void turnoutLabels() {
        turnLabel.setText("Tour actuel : " + sea.getTurn());
        tunaLabel.setText("Thons : " + sea.getTunaCount());
        sharkLabel.setText("Requins : " + sea.getSharkCount());
        emptyLabel.setText("Cases vides : " + sea.getEmptyCount());
    }

    private void refresh() {
        turnoutLabels();
        drawSea();
        addHistoryPoint();
    }

    private void addHistoryPoint() {
        if (tunaSeries.getData().size() > 300) {
            tunaSeries.getData().remove(0);
        }
        if (sharkSeries.getData().size() > 300) {
            sharkSeries.getData().remove(0);
        }
        tunaSeries.getData().add(new XYChart.Data<>(sea.getTurn(), sea.getTunaCount()));
        sharkSeries.getData().add(new XYChart.Data<>(sea.getTurn(), sea.getSharkCount()));
    }

    private void drawSea() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int cellSize = Sea.CELL_SIZE;
        int width = sea.getWidth() * cellSize;
        int height = sea.getHeight() * cellSize;
        canvas.setWidth(width);
        canvas.setHeight(height);

        gc.setFill(Color.web("#12314c"));
        gc.fillRect(0, 0, width, height);

        for (int y = 0; y < sea.getHeight(); y++) {
            for (int x = 0; x < sea.getWidth(); x++) {
                Position position = new Position(x, y);
                Animal animal = sea.animalAt(position);
                if (animal == null) {
                    gc.setFill(Color.web("#12314c"));
                } else if (animal.isTuna()) {
                    gc.setFill(Color.web("#44bb66"));
                } else {
                    gc.setFill(Color.web("#dd4b39"));
                }
                gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }
}

package view;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.ParticleWorld;

import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        int numberOfParticles = 150;
        int radius = 8;

        ParticleWorld world = new ParticleWorld(width, height, numberOfParticles, radius);
        List<ParticleView> particleViews = world.getParticles().stream()
                .map(ParticleView::new)
                .toList();
        Pane pane = new Pane();

        for (ParticleView particleView : particleViews) {
            pane.getChildren().add(particleView.getNode());
        }

        pane.setFocusTraversable(true);
        stage.setTitle("ScreenJavaFX");
        Scene scene = new Scene(pane, width, height);
        scene.setFill(Color.rgb(20, 20, 20));
        stage.setScene(scene);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.show();
        pane.requestFocus();

        long[] lastFrameTime = {0};
        AnimationTimer animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double deltaSeconds = lastFrameTime[0] == 0
                        ? 0
                        : (now - lastFrameTime[0]) / 1_000_000_000.0;
                lastFrameTime[0] = now;

                world.update(deltaSeconds);
                for (ParticleView particleView : particleViews) {
                    particleView.update();
                }
            }
        };

        boolean[] paused = {false};
        boolean[] spacePressed = {false};
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE && !spacePressed[0]) {
                spacePressed[0] = true;
                paused[0] = !paused[0];
                if (paused[0]) {
                    animation.stop();
                } else {
                    lastFrameTime[0] = 0;
                    animation.start();
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                animation.stop();
                Platform.exit();
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                spacePressed[0] = false;
            }
        });

        animation.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Particle;
import model.ParticleColor;

public class ParticleView {
    private final Particle particle;
    private final Circle circle;

    public ParticleView(Particle particle) {
        this.particle = particle;
        circle = new Circle(particle.getX(), particle.getY(), particle.getRadius());
        update();
    }

    public Circle getNode() {
        return circle;
    }

    public void update() {
        circle.setCenterX(particle.getX());
        circle.setCenterY(particle.getY());
        circle.setFill(toFxColor(particle.getFillColor()));
    }

    private Color toFxColor(ParticleColor color) {
        return Color.color(color.red(), color.green(), color.blue());
    }
}

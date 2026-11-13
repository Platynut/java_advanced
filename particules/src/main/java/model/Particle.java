package model;

import java.util.Random;

public class Particle {
    private static final double SPEED_PIXELS_PER_SECOND = 150.0;

    private double x;
    private double y;
    private final double radius;
    private double dx;
    private double dy;
    private ParticleColor fillColor;
    private ParticleColor strokeColor;
    private final Random random;

    Particle(double x, double y, double radius, Random random) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.random = random;
        fillColor = randomColor();
        strokeColor = randomColor();
        randomDirection();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public ParticleColor getFillColor() {
        return fillColor;
    }

    public ParticleColor getStrokeColor() {
        return strokeColor;
    }

    void move(double width, double height, double deltaSeconds) {
        x += dx * deltaSeconds;
        y += dy * deltaSeconds;

        if (x > width + radius) {
            x = -radius;
        } else if (x < -radius) {
            x = width + radius;
        }

        if (y > height + radius) {
            y = -radius;
        } else if (y < -radius) {
            y = height + radius;
        }
    }

    void collideWith(Particle other) {
        double diffX = other.x - x;
        double diffY = other.y - y;
        double distance = Math.hypot(diffX, diffY);
        double minDistance = radius + other.radius;

        if (distance >= minDistance) {
            return;
        }

        double normalX = distance == 0 ? 1 : diffX / distance;
        double normalY = distance == 0 ? 0 : diffY / distance;
        double overlap = minDistance - distance + 0.01;

        x -= normalX * overlap / 2;
        y -= normalY * overlap / 2;
        other.x += normalX * overlap / 2;
        other.y += normalY * overlap / 2;

        double relativeNormalSpeed = (other.dx - dx) * normalX
                + (other.dy - dy) * normalY;

        if (relativeNormalSpeed < 0) {
            dx += relativeNormalSpeed * normalX;
            dy += relativeNormalSpeed * normalY;
            other.dx -= relativeNormalSpeed * normalX;
            other.dy -= relativeNormalSpeed * normalY;

            changeColor();
            other.changeColor();
        }
    }

    private void randomDirection() {
        Directions direction = Directions.values()[random.nextInt(Directions.values().length)];
        setDirection(direction);
    }

    private void setDirection(Directions direction) {
        double length = Math.hypot(direction.getX(), direction.getY());
        dx = direction.getX() / length * SPEED_PIXELS_PER_SECOND;
        dy = direction.getY() / length * SPEED_PIXELS_PER_SECOND;
    }

    private void changeColor() {
        fillColor = randomColor();
        strokeColor = randomColor();
    }

    private ParticleColor randomColor() {
        return new ParticleColor(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }
}

package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleWorld {
    private static final int MAX_PLACEMENT_ATTEMPTS = 10_000;

    private final double width;
    private final double height;
    private final List<Particle> particles;
    private final Random random = new Random();

    public ParticleWorld(double width, double height, int numberOfParticles, double radius) {
        if (!Double.isFinite(width) || !Double.isFinite(height)
                || !Double.isFinite(radius) || numberOfParticles < 0 || radius < 0) {
            throw new IllegalArgumentException("Dimensions, rayon ou nombre de particules invalide.");
        }
        if (width <= 2.0 * radius || height <= 2.0 * radius) {
            throw new IllegalArgumentException("L'écran est trop petit pour placer les particules.");
        }

        this.width = width;
        this.height = height;
        List<Particle> createdParticles = new ArrayList<>();

        for (int i = 0; i < numberOfParticles; i++) {
            Particle particle = createParticle(createdParticles, radius);
            createdParticles.add(particle);
        }

        particles = List.copyOf(createdParticles);
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void update(double deltaSeconds) {
        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0) {
            throw new IllegalArgumentException("Le temps écoulé doit être positif ou nul.");
        }

        for (Particle particle : particles) {
            particle.move(width, height, deltaSeconds);
        }

        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                particles.get(i).collideWith(particles.get(j));
            }
        }
    }

    private Particle createParticle(List<Particle> placedParticles, double radius) {
        for (int attempt = 0; attempt < MAX_PLACEMENT_ATTEMPTS; attempt++) {
            double x = random.nextDouble(radius, width - radius);
            double y = random.nextDouble(radius, height - radius);

            if (!overlapsExistingParticle(placedParticles, x, y, radius)) {
                return new Particle(x, y, radius, random);
            }
        }

        throw new IllegalStateException(
                "Impossible de placer toutes les particules sans chevauchement sur cette surface."
        );
    }

    private boolean overlapsExistingParticle(
            List<Particle> placedParticles,
            double x,
            double y,
            double radius
    ) {
        for (Particle existing : placedParticles) {
            if (Math.hypot(existing.getX() - x, existing.getY() - y)
                    <= existing.getRadius() + radius) {
                return true;
            }
        }
        return false;
    }
}

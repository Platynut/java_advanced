package model;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Animal {
    private Position position;
    private int reproductionAge;
    private int hunger;
    private boolean alive = true;

    protected Animal(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getReproductionAge() {
        return reproductionAge;
    }

    public void incrementReproductionAge() {
        reproductionAge++;
    }

    public void resetReproductionAge() {
        reproductionAge = 0;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public void incrementHunger() {
        hunger++;
    }

    public void resetHunger() {
        hunger = 0;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public abstract boolean isTuna();

    public abstract boolean isShark();

    protected abstract void actInSea(Sea sea);

    protected Position chooseRandom(List<Position> positions) {
        if (positions == null || positions.isEmpty()) {
            return null;
        }
        return positions.get(ThreadLocalRandom.current().nextInt(positions.size()));
    }

    public final void act(Sea sea) {
        if (alive) {
            actInSea(sea);
        }
    }
}

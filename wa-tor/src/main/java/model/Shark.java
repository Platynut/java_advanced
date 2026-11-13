package model;

import java.util.List;

public class Shark extends Animal {
    public Shark(Position position) {
        super(position);
    }

    @Override
    public boolean isTuna() {
        return false;
    }

    @Override
    public boolean isShark() {
        return true;
    }

    @Override
    protected void actInSea(Sea sea) {
        Position current = getPosition();
        List<Position> preyPositions = sea.tunaNeighborPositions(current);
        Position destination = null;

        if (!preyPositions.isEmpty()) {
            destination = chooseRandom(preyPositions);
            sea.eatAnimal(this, destination);
            resetHunger();
        } else {
            List<Position> emptyNeighbors = sea.emptyNeighborPositions(current);
            destination = chooseRandom(emptyNeighbors);
            if (destination != null) {
                sea.moveAnimal(this, destination);
            }
            incrementHunger();
        }

        if (getHunger() >= Sea.SHARK_STARVATION_TURNS) {
            sea.removeAnimal(this);
            return;
        }

        boolean moved = destination != null;
        if (moved && getReproductionAge() >= Sea.SHARK_REPRODUCTION_INTERVAL) {
            sea.placeAnimal(new Shark(current), current);
            resetReproductionAge();
        } else {
            incrementReproductionAge();
        }
    }
}

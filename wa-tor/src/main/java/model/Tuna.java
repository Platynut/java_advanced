package model;

import java.util.List;

public class Tuna extends Animal {
    public Tuna(Position position) {
        super(position);
    }

    @Override
    public boolean isTuna() {
        return true;
    }

    @Override
    public boolean isShark() {
        return false;
    }

    @Override
    protected void actInSea(Sea sea) {
        Position current = getPosition();
        List<Position> emptyNeighbors = sea.emptyNeighborPositions(current);
        Position destination = chooseRandom(emptyNeighbors);
        boolean moved = destination != null;

        if (moved) {
            sea.moveAnimal(this, destination);
        }

        if (moved && getReproductionAge() >= Sea.TUNA_REPRODUCTION_INTERVAL) {
            sea.placeAnimal(new Tuna(current), current);
            resetReproductionAge();
        } else {
            incrementReproductionAge();
        }
    }
}

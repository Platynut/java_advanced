package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sea {
    public static final int CELL_SIZE = 10;
    public static final int TUNA_REPRODUCTION_INTERVAL = 3;
    public static final int SHARK_REPRODUCTION_INTERVAL = 10;
    public static final int SHARK_STARVATION_TURNS = 3;
    public static final double INITIAL_TUNA_PROPORTION = 0.30;
    public static final double INITIAL_SHARK_PROPORTION = 0.03;

    private final int width;
    private final int height;
    private final Animal[][] cells;
    private final List<Animal> animals = new ArrayList<>();
    private int currentTurn;
    private final List<Integer> tunaHistory = new ArrayList<>();
    private final List<Integer> sharkHistory = new ArrayList<>();

    public Sea(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Animal[height][width];
    }

    public void initialize() {
        animals.clear();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = null;
            }
        }

        List<Position> positions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                positions.add(new Position(x, y));
            }
        }
        Collections.shuffle(positions);

        int totalCells = width * height;
        int tunaCount = Math.max(0, (int) Math.round(totalCells * INITIAL_TUNA_PROPORTION));
        int sharkCount = Math.max(0, (int) Math.round(totalCells * INITIAL_SHARK_PROPORTION));

        for (int i = 0; i < tunaCount; i++) {
            placeAnimal(new Tuna(positions.get(i)), positions.get(i));
        }
        for (int i = tunaCount; i < tunaCount + sharkCount; i++) {
            placeAnimal(new Shark(positions.get(i)), positions.get(i));
        }

        updateHistory();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTurn() {
        return currentTurn;
    }

    public int getTotalCells() {
        return width * height;
    }

    public int getEmptyCount() {
        return getTotalCells() - animals.size();
    }

    public Animal animalAt(Position position) {
        if (position == null) {
            return null;
        }
        int wrappedX = wrapIndex(position.getX(), width);
        int wrappedY = wrapIndex(position.getY(), height);
        return cells[wrappedY][wrappedX];
    }

    public boolean isEmpty(Position position) {
        return animalAt(position) == null;
    }

    public List<Position> neighborPositions(Position position) {
        List<Position> neighbors = new ArrayList<>();
        neighbors.add(new Position(position.getX() + 1, position.getY()));
        neighbors.add(new Position(position.getX() - 1, position.getY()));
        neighbors.add(new Position(position.getX(), position.getY() + 1));
        neighbors.add(new Position(position.getX(), position.getY() - 1));
        return neighbors.stream()
                .map(this::wrapPosition)
                .toList();
    }

    public List<Position> emptyNeighborPositions(Position position) {
        List<Position> empty = new ArrayList<>();
        for (Position neighbor : neighborPositions(position)) {
            if (isEmpty(neighbor)) {
                empty.add(neighbor);
            }
        }
        return empty;
    }

    public List<Position> tunaNeighborPositions(Position position) {
        List<Position> tunaNeighbors = new ArrayList<>();
        for (Position neighbor : neighborPositions(position)) {
            Animal occupant = animalAt(neighbor);
            if (occupant != null && occupant.isTuna()) {
                tunaNeighbors.add(neighbor);
            }
        }
        return tunaNeighbors;
    }

    public void advanceTurn() {
        currentTurn++;
        List<Animal> snapshot = new ArrayList<>(animals);
        Collections.shuffle(snapshot);
        for (Animal animal : snapshot) {
            if (animal != null && animal.isAlive()) {
                animal.act(this);
            }
        }
        animals.removeIf(animal -> !animal.isAlive());
        updateHistory();
    }

    public void placeAnimal(Animal animal, Position position) {
        if (animal == null || position == null) {
            return;
        }
        Position wrapped = wrapPosition(position);
        cells[wrapped.getY()][wrapped.getX()] = animal;
        animal.setPosition(wrapped);
        if (!animals.contains(animal)) {
            animals.add(animal);
        }
    }

    public void moveAnimal(Animal animal, Position target) {
        if (animal == null || target == null) {
            return;
        }
        Position destination = wrapPosition(target);
        Position previous = animal.getPosition();
        if (previous == null || previous.equals(destination)) {
            return;
        }

        cells[wrapIndex(previous.getY(), height)][wrapIndex(previous.getX(), width)] = null;
        animal.setPosition(destination);
        cells[destination.getY()][destination.getX()] = animal;
    }

    public void removeAnimal(Animal animal) {
        if (animal == null) {
            return;
        }
        Position position = animal.getPosition();
        if (position != null) {
            cells[position.getY()][position.getX()] = null;
        }
        animal.setAlive(false);
        animals.remove(animal);
    }

    public void eatAnimal(Animal predator, Position preyPosition) {
        if (predator == null || preyPosition == null) {
            return;
        }
        Position wrappedPrey = wrapPosition(preyPosition);
        Animal prey = animalAt(wrappedPrey);
        if (prey != null && prey.isTuna()) {
            removeAnimal(prey);
        }
        moveAnimal(predator, wrappedPrey);
    }

    public int getTunaCount() {
        int counter = 0;
        for (Animal animal : animals) {
            if (animal != null && animal.isAlive() && animal.isTuna()) {
                counter++;
            }
        }
        return counter;
    }

    public int getSharkCount() {
        int counter = 0;
        for (Animal animal : animals) {
            if (animal != null && animal.isAlive() && animal.isShark()) {
                counter++;
            }
        }
        return counter;
    }

    public List<Integer> getTunaHistory() {
        return List.copyOf(tunaHistory);
    }

    public List<Integer> getSharkHistory() {
        return List.copyOf(sharkHistory);
    }

    public List<Animal> getAnimals() {
        return List.copyOf(animals);
    }

    private Position wrapPosition(Position position) {
        return new Position(wrapIndex(position.getX(), width), wrapIndex(position.getY(), height));
    }

    private int wrapIndex(int coordinate, int size) {
        int wrapped = coordinate % size;
        if (wrapped < 0) {
            wrapped += size;
        }
        return wrapped;
    }

    private void updateHistory() {
        tunaHistory.add(getTunaCount());
        sharkHistory.add(getSharkCount());
        if (tunaHistory.size() > 300) {
            tunaHistory.remove(0);
        }
        if (sharkHistory.size() > 300) {
            sharkHistory.remove(0);
        }
    }
}

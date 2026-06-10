package ba.sum.fsre.diplomski.maze.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Cell implements Serializable {
    private final int row;
    private final int col;

    private final Map<Direction, Boolean> walls;

    private boolean visited;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.visited = false;

        this.walls = new EnumMap<>(Direction.class);

        // Na početku svi zidovi postoje
        for (Direction direction : Direction.values()) {
            walls.put(direction, true);
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    // Provjerava postoji li zid u zadanom smjeru.
    public boolean hasWall(Direction direction) {
        return walls.get(direction);
    }

    // Postavlja stanje zida.
    public void setWall(Direction direction, boolean exists) {
        walls.put(direction, exists);
    }

    // Uklanja zid u zadanom smjeru.
    public void removeWall(Direction direction) {
        walls.put(direction, false);
    }

    // Vraća sve zidove ćelije.
    public Map<Direction, Boolean> getWalls() {
        return walls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Cell cell)) return false;

        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Cell{" + "row=" + row + ", col=" + col + '}';
    }
}

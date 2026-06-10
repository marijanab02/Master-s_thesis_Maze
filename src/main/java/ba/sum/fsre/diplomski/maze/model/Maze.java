package ba.sum.fsre.diplomski.maze.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Maze implements Serializable {
    private final int rows;
    private final int cols;

    private final Cell[][] grid;

    private Cell start;
    private Cell goal;

    public Maze(int rows, int cols) {

        if (rows<=0 || cols<=0) {throw new IllegalArgumentException("Dimenzije moraju biti veće od 0");}

        this.rows = rows;
        this.cols = cols;

        this.grid = new Cell[rows][cols];

        initializeGrid();

        this.start = grid[0][0];
        this.goal = grid[rows-1][cols-1];
    }

    // Inicijalizira mrežu ćelija.
    private void initializeGrid() {
        for (int row=0; row<rows; row++) {
            for (int col=0; col<cols; col++) {
                grid[row][col] = new Cell(row, col);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Cell getCell(int row, int col) {
        if (!isInsideBounds(row, col)) {
            return null;
        }

        return grid[row][col];
    }

    public Cell getStart() {
        return start;
    }

    public void setStart(Cell start) {
        this.start = start;
    }

    public Cell getGoal() {
        return goal;
    }

    public void setGoal(Cell goal) {
        this.goal = goal;
    }

    //Provjerava nalazi li se pozicija unutar granica labirinta.
    public boolean isInsideBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    // Provjerava može li se napraviti pomak iz ćelije u zadanom smjeru.
    public boolean isValidMove(Cell current, Direction direction) {

        if (current==null || direction==null) {
            return false;
        }

        // Ako postoji zid nije moguće proći
        if (current.hasWall(direction)) {
            return false;
        }

        int newRow = current.getRow() + direction.getRowOffset();
        int newCol = current.getCol() + direction.getColOffset();

        return isInsideBounds(newRow, newCol);
    }

    // Dohvaća susjedne ćelije bez zidova.
    public List<Cell> getReachableNeighbors(Cell current) {

        List<Cell> neighbors = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            if (isValidMove(current, direction)) {
                int newRow = current.getRow() + direction.getRowOffset();
                int newCol = current.getCol() + direction.getColOffset();

                neighbors.add(getCell(newRow, newCol));
            }
        }

        return neighbors;
    }
}

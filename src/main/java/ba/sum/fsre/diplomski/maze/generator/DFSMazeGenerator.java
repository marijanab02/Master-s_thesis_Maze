package ba.sum.fsre.diplomski.maze.generator;

import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Direction;
import ba.sum.fsre.diplomski.maze.model.Maze;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Generator labirinta temeljen na Randomized DFS algoritmu
// Algoritam generira "savršeni labirint", između bilo koje dvije ćelije postoji točno jedan put.

public class DFSMazeGenerator implements MazeGenerator {

    private static final Logger logger =
            LoggerFactory.getLogger(DFSMazeGenerator.class);

    @Override
    public Maze generate(int rows, int cols) {
        long seed = System.nanoTime();
        return generate(rows, cols, seed);
    }

    @Override
    public Maze generate(int rows, int cols, long seed) {
        System.out.println("Pokretanje DFS generatora labirinta "+ rows + "x" + cols + " sa seed=" + seed);

        Maze maze = new Maze(rows, cols);
        Random random = new Random(seed);

        boolean[][] visited = new boolean[rows][cols];
        Stack<Cell> stack = new Stack<>();

        // Nasumična početna ćelija
        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);

        Cell startCell = maze.getCell(startRow, startCol);

        visited[startRow][startCol] = true;

        stack.push(startCell);

        int visitedCount = 1;
        while (!stack.isEmpty()) {

            Cell current = stack.peek();
            List<Neighbor> unvisitedNeighbors = getUnvisitedNeighbors(maze, current, visited);

            if (!unvisitedNeighbors.isEmpty()) {

                Collections.shuffle(unvisitedNeighbors, random);
                Neighbor selected = unvisitedNeighbors.get(0);

                Cell next = selected.cell();
                Direction direction = selected.direction();

                removeWalls(current, next, direction);
                visited[next.getRow()][next.getCol()] = true;
                stack.push(next);
                visitedCount++;

            } else {
                // Backtracking
                stack.pop();
            }
        }
        System.out.println("Generiranje završeno. Posjećeno ćelija: " + visitedCount);

        // Start i cilj postavljeni na suprotne krajeve
        maze.setStart(maze.getCell(0, 0));
        maze.setGoal(maze.getCell(rows - 1, cols - 1));

        return maze;
    }

    //Dohvaca ne posjecene susjede trenutne ćelije
    private List<Neighbor> getUnvisitedNeighbors(Maze maze, Cell current, boolean[][] visited) {

        List<Neighbor> neighbors = new ArrayList<>(4);

        for (Direction direction : Direction.values()) {
            int newRow = current.getRow() + direction.getRowOffset();
            int newCol = current.getCol() + direction.getColOffset();

            if (!maze.isInsideBounds(newRow, newCol)) {
                continue;
            }

            if (!visited[newRow][newCol]) {
                Cell neighbor = maze.getCell(newRow, newCol);
                neighbors.add(new Neighbor(neighbor, direction));
            }
        }

        return neighbors;
    }

    // Uklanja zidove između dvije susjedne ćelije
    private void removeWalls(Cell current, Cell next, Direction direction) {
        current.removeWall(direction);
        next.removeWall(direction.opposite());
    }

    @Override
    public String getName() {
        return "Randomized DFS (Recursive Backtracking)";
    }


    //Pomoćni record za spremanje susjeda i smjera.
    private record Neighbor(Cell cell, Direction direction) {
    }
}
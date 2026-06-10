package ba.sum.fsre.diplomski.maze.generator;


import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Maze;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class test {

    private final MazeGenerator generator =
            new DFSMazeGenerator();

    @Test
    void shouldGenerateMazeWithCorrectDimensions() {

        Maze maze = generator.generate(10, 15);

        assertEquals(10, maze.getRows());
        assertEquals(15, maze.getCols());
    }

    @Test
    void shouldSetStartAndGoal() {

        Maze maze = generator.generate(10, 10);

        assertNotNull(maze.getStart());
        assertNotNull(maze.getGoal());
    }

    @Test
    void shouldGenerateReachableMaze() {

        Maze maze = generator.generate(20, 20);

        Set<Cell> visited = new HashSet<>();

        Queue<Cell> queue = new LinkedList<>();

        queue.add(maze.getStart());

        visited.add(maze.getStart());

        while (!queue.isEmpty()) {

            Cell current = queue.poll();

            for (Cell neighbor :
                    maze.getReachableNeighbors(current)) {

                if (!visited.contains(neighbor)) {

                    visited.add(neighbor);

                    queue.add(neighbor);
                }
            }
        }

        assertEquals(
                maze.getRows() * maze.getCols(),
                visited.size()
        );
    }

    @Test
    void shouldHavePathFromStartToGoal() {

        Maze maze = generator.generate(5, 5);

        Set<Cell> visited = new HashSet<>();

        Queue<Cell> queue = new LinkedList<>();

        queue.add(maze.getStart());

        visited.add(maze.getStart());

        boolean found = false;

        while (!queue.isEmpty()) {

            Cell current = queue.poll();

            if (current.equals(maze.getGoal())) {

                found = true;

                break;
            }

            for (Cell neighbor :
                    maze.getReachableNeighbors(current)) {

                if (!visited.contains(neighbor)) {

                    visited.add(neighbor);

                    queue.add(neighbor);
                }
            }
        }

        assertTrue(found);
    }

    @Test
    void shouldGenerateSameMazeWithSameSeed() {

        Maze maze1 = generator.generate(10, 10, 123L);

        Maze maze2 = generator.generate(10, 10, 123L);

        for (int row = 0; row < 10; row++) {

            for (int col = 0; col < 10; col++) {

                Cell c1 = maze1.getCell(row, col);
                Cell c2 = maze2.getCell(row, col);

                for (var direction :
                        ba.sum.fsre.diplomski.maze.model.Direction.values()) {

                    assertEquals(
                            c1.hasWall(direction),
                            c2.hasWall(direction)
                    );
                }
            }
        }
    }
}
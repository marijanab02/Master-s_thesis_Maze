package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.generator.DFSMazeGenerator;
import ba.sum.fsre.diplomski.maze.generator.MazeGenerator;
import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Direction;
import ba.sum.fsre.diplomski.maze.model.Maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DFSAgentTest {

    private final DFSAgent agent = new DFSAgent();

    @Test
    void testShouldFindPathInSimpleMaze() {

        Maze maze = new Maze(2, 2);

        Cell c00 = maze.getCell(0, 0);
        Cell c01 = maze.getCell(0, 1);
        Cell c11 = maze.getCell(1, 1);

        c00.removeWall(Direction.RIGHT);
        c01.removeWall(Direction.LEFT);

        c01.removeWall(Direction.DOWN);
        c11.removeWall(Direction.UP);

        maze.setStart(c00);
        maze.setGoal(c11);

        SearchResult result = agent.solve(maze);

        assertTrue(result.isPathFound());

        assertNotNull(result.getPath());

        assertTrue(result.getPathLength() > 0);
    }

    @Test
    void testShouldReturnFailureWhenNoPath() {

        Maze maze = new Maze(2, 2);

        maze.setStart(maze.getCell(0, 0));

        maze.setGoal(maze.getCell(1, 1));

        SearchResult result = agent.solve(maze);

        assertFalse(result.isPathFound());

        assertEquals(0, result.getPathLength());
    }

    @Test
    void testShouldMeasureTimeAndMemory() {

        MazeGenerator generator =
                new DFSMazeGenerator();

        Maze maze = generator.generate(10, 10);

        SearchResult result = agent.solve(maze);

        assertTrue(result.getExecutionTimeNs() > 0);

        assertTrue(result.getMemoryUsedBytes() >= 0);
    }

    @Test
    void testShouldFindPathInGeneratedMaze() {

        MazeGenerator generator = new DFSMazeGenerator();

        Maze maze = generator.generate(20, 20);

        SearchResult result = agent.solve(maze);

        assertTrue(result.isPathFound());

        assertNotNull(result.getPath());

        assertEquals(maze.getGoal(), result.getPath().get(result.getPath().size() - 1));
    }

    @Test
    void testShouldReturnCorrectAlgorithmType() {

        assertEquals(AlgorithmType.DFS, agent.getType());
    }

    @Test
    void testShouldReturnCorrectName() {

        assertEquals("Depth-First Search", agent.getName());
    }
}
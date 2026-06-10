package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.generator.DFSMazeGenerator;
import ba.sum.fsre.diplomski.maze.generator.MazeGenerator;
import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Direction;
import ba.sum.fsre.diplomski.maze.model.Maze;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testovi za BFSAgent.
 */
class BFSAgentTest {

    /**
     * Test provjerava da BFS pronađe put
     * u jednostavnom ručno kreiranom labirintu.
     */
    @Test
    void testShouldFindPathInSimpleMaze() {

        Maze maze = new Maze(3, 3);

        openPath(maze);

        BFSAgent agent = new BFSAgent();

        SearchResult result = agent.solve(maze);

        assertTrue(result.isPathFound());

        assertNotNull(result.getPath());

        assertFalse(result.getPath().isEmpty());
    }

    /**
     * BFS mora pronaći najkraći put.
     */
    @Test
    void testShouldFindShortestPath() {

        Maze maze = new Maze(3, 3);

        openPath(maze);

        BFSAgent agent = new BFSAgent();

        SearchResult result = agent.solve(maze);

        List<Cell> path = result.getPath();

        /*
         * Očekivani optimalni put:
         *
         * (0,0)
         * (0,1)
         * (0,2)
         * (1,2)
         * (2,2)
         *
         * = 5 ćelija
         */
        assertEquals(5, path.size());
    }

    /**
     * Testira BFS na generiranom labirintu.
     */
    @Test
    void testShouldFindPathInGeneratedMaze() {

        MazeGenerator generator = new DFSMazeGenerator();

        Maze maze = generator.generate(20, 20);

        BFSAgent agent = new BFSAgent();

        SearchResult result = agent.solve(maze);

        assertTrue(result.isPathFound());

        assertNotNull(result.getPath());

        assertFalse(result.getPath().isEmpty());
    }

    /**
     * Provjera mjerenja vremena i memorije.
     */
    @Test
    void testShouldMeasureTimeAndMemory() {

        MazeGenerator generator = new DFSMazeGenerator();

        Maze maze = generator.generate(10, 10);

        BFSAgent agent = new BFSAgent();

        SearchResult result = agent.solve(maze);

        assertTrue(result.getExecutionTimeNs() > 0);

        assertTrue(result.getMemoryUsedBytes() >= 0);
    }

    /**
     * Test exploration order liste.
     */
    @Test
    void testShouldStoreExplorationOrder() {

        MazeGenerator generator = new DFSMazeGenerator();

        Maze maze = generator.generate(10, 10);

        BFSAgent agent = new BFSAgent();

        agent.solve(maze);

        List<Cell> exploration =
                agent.getExplorationOrder();

        assertNotNull(exploration);

        assertFalse(exploration.isEmpty());
    }

    /**
     * Kreira jednostavan prolaz:
     *
     * S -> ->
     *      ↓
     *      ↓
     *      G
     */
    private void openPath(Maze maze) {

        Cell c00 = maze.getCell(0, 0);
        Cell c01 = maze.getCell(0, 1);
        Cell c02 = maze.getCell(0, 2);

        Cell c12 = maze.getCell(1, 2);
        Cell c22 = maze.getCell(2, 2);

        removeWalls(c00, c01, Direction.RIGHT);
        removeWalls(c01, c02, Direction.RIGHT);
        removeWalls(c02, c12, Direction.DOWN);
        removeWalls(c12, c22, Direction.DOWN);

        maze.setStart(c00);
        maze.setGoal(c22);
    }

    /**
     * Uklanja zidove između dvije susjedne ćelije.
     */
    private void removeWalls(
            Cell current,
            Cell neighbor,
            Direction direction
    ) {

        current.removeWall(direction);
        neighbor.removeWall(direction.opposite());
    }
}
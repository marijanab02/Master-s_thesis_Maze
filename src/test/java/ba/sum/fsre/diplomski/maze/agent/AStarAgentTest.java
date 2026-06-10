package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.algorithm.ManhattanHeuristic;
import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.generator.DFSMazeGenerator;
import ba.sum.fsre.diplomski.maze.generator.MazeGenerator;
import ba.sum.fsre.diplomski.maze.model.Maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AStarAgentTest {

    @Test
    void shouldFindPathInGeneratedMaze() {

        MazeGenerator generator = new DFSMazeGenerator();

        Maze maze = generator.generate(20, 20);

        AStarAgent agent = new AStarAgent(new ManhattanHeuristic());

        SearchResult result = agent.solve(maze);

        assertTrue(result.isPathFound());

        assertNotNull(result.getPath());

        assertFalse(result.getPath().isEmpty());
    }

    @Test
    void shouldExploreLessThanBFS() {

        MazeGenerator generator = new DFSMazeGenerator();

        Maze maze = generator.generate(20, 20);

        BFSAgent bfs = new BFSAgent();

        AStarAgent astar = new AStarAgent(new ManhattanHeuristic());

        SearchResult bfsResult = bfs.solve(maze);

        SearchResult astarResult = astar.solve(maze);

        assertTrue(astarResult.getNodesExplored() <= bfsResult.getNodesExplored());
    }

    @Test
    void shouldFindOptimalPathLikeBFS() {

        MazeGenerator generator = new DFSMazeGenerator();

        Maze maze = generator.generate(20, 20);

        BFSAgent bfs = new BFSAgent();

        AStarAgent astar =
                new AStarAgent(new ManhattanHeuristic());

        SearchResult bfsResult = bfs.solve(maze);

        SearchResult astarResult = astar.solve(maze);

        assertEquals(bfsResult.getPathLength(), astarResult.getPathLength());
    }
}
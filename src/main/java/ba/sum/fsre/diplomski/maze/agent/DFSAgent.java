package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Maze;

import java.util.*;

//DFS agent
public class DFSAgent implements Agent {

    private final List<Cell> explorationOrder;

    public DFSAgent() {
        this.explorationOrder = new ArrayList<>();
    }

    @Override
    public SearchResult solve(Maze maze) {

        Objects.requireNonNull(maze);

        explorationOrder.clear();
        System.out.println("Pokretanje DFS algoritma.");

        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();

        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        Cell start = maze.getStart();
        Cell goal = maze.getGoal();

        Stack<Cell> stack = new Stack<>();
        Set<Cell> visited = new HashSet<>();
        Map<Cell, Cell> parentMap = new HashMap<>();

        int nodesExplored = 0;

        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {

            Cell current = stack.pop();
            explorationOrder.add(current);

            nodesExplored++;

            // System.out.printf("Korak %d: Posjećujem ćeliju (%d, %d)%n", nodesExplored, current.getRow(), current.getCol());

            if (current.equals(goal)) {

                System.out.println("Cilj pronađen.");

                List<Cell> path = reconstructPath(parentMap, goal);

                long endTime = System.nanoTime();
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

                return SearchResult.success(path,getName(), nodesExplored, endTime - startTime, Math.max(0, memoryAfter - memoryBefore));
            }

            for (Cell neighbor : maze.getReachableNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }

        long endTime = System.nanoTime();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Put nije pronađen");

        return SearchResult.failure(getName(),nodesExplored, endTime - startTime, Math.max(0, memoryAfter - memoryBefore));
    }

    // Rekonstrukcija puta od cilja do starta.
    private List<Cell> reconstructPath(Map<Cell, Cell> parentMap, Cell goal) {

        LinkedList<Cell> path = new LinkedList<>();
        Cell current = goal;

        while (current != null) {
            path.addFirst(current);
            current = parentMap.get(current);
        }

        return path;
    }

    public List<Cell> getExplorationOrder() {
        return new ArrayList<>(explorationOrder);
    }

    @Override
    public String getName() {
        return "Depth-First Search";
    }

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.DFS;
    }
}
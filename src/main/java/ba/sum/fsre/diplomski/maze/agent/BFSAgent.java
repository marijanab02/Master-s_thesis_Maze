package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Maze;

import java.util.*;


//BFS (Breadth-First Search) agent, istražuje labirint po razinama (level-order traversal)
// Koristi: Queue<Cell> za FIFO obradu, HashSet<Cell> za visited čvorove, HashMap<Cell, Cell> za rekonstrukciju puta
// optimalni baseline za usporedbu
public class BFSAgent implements Agent {

    // Redoslijed posjećenih ćelija.
    private final List<Cell> explorationOrder;

    public BFSAgent() {
        this.explorationOrder = new ArrayList<>();
    }

    @Override
    public SearchResult solve(Maze maze) {
        Objects.requireNonNull(maze, "Maze ne smije biti null.");

        System.out.println("BFS algoritam započinje rješavanje labirinta.");

        explorationOrder.clear();

        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        Cell start = maze.getStart();
        Cell goal = maze.getGoal();

        Queue<Cell> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();
        Map<Cell, Cell> parentMap = new HashMap<>();

        int nodesExplored = 0;

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {

            Cell current = queue.poll();
            nodesExplored++;
            explorationOrder.add(current);
            if (current.equals(goal)) {

                List<Cell> path = reconstructPath(parentMap, start, goal);

                long executionTime = System.nanoTime() - startTime;
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                long memoryUsed = Math.max(0, memoryAfter - memoryBefore);

                System.out.println("BFS završio. Put pronađen. Dužina puta: " + path.size() + "istrazeno cvorova " + nodesExplored);

                return SearchResult.success(path, getName(), nodesExplored, executionTime, memoryUsed);
            }

            //BFS istražuje sve susjede trenutne ćelije
            List<Cell> neighbors = maze.getReachableNeighbors(current);

            for (Cell neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        //Nije pronađen put
        long executionTime = System.nanoTime() - startTime;
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.max(0, memoryAfter - memoryBefore);

        System.out.println("BFS nije pronašao put kroz labirint.");

        return SearchResult.failure(getName(), nodesExplored, executionTime, memoryUsed);
    }

    //Rekonstrukcija puta od cilja prema startu.
    private List<Cell> reconstructPath(Map<Cell, Cell> parentMap, Cell start, Cell goal) {

        LinkedList<Cell> path = new LinkedList<>();

        Cell current = goal;

        while (current != null) {
            path.addFirst(current);
            if (current.equals(start)) {
                break;
            }
            current = parentMap.get(current);
        }

        return path;
    }

    //Vraća redoslijed istraživanja ćelija. Koristi se za animaciju

    public List<Cell> getExplorationOrder() {
        return new ArrayList<>(explorationOrder);
    }

    @Override
    public String getName() {
        return "Breadth-First Search";
    }

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.BFS;
    }
}
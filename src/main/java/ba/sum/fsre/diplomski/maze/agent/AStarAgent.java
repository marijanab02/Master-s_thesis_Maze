package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.algorithm.Heuristic;
import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Direction;
import ba.sum.fsre.diplomski.maze.model.Maze;

import java.util.*;

//A* agent za pronalazak optimalnog puta kroz labirint.
//A* koristi: f(n)=g(n)+h(n), g(n) = stvarna udaljenost od starta, h(n) = heuristička procjena do cilja
public class AStarAgent implements Agent {

    private final Heuristic heuristic;

    private final List<Cell> explorationOrder;

    public AStarAgent(Heuristic heuristic) {
        this.heuristic = heuristic;
        this.explorationOrder = new ArrayList<>();
    }

    @Override
    public SearchResult solve(Maze maze) {
        System.out.println("A* algoritam započinje, heuristika=" + heuristic.getName());

        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        explorationOrder.clear();

        Cell start= maze.getStart();
        Cell goal = maze.getGoal();

        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<Cell> closedSet = new HashSet<>();
        Map<Cell, Cell> parentMap = new HashMap<>();
        Map<Cell, Double> gScores = new HashMap<>();

        int nodesExplored = 0;

        double h = heuristic.calculate(start, goal);

        openSet.add(new AStarNode(start, 0, h));

        gScores.put(start, 0.0);

        while (!openSet.isEmpty()) {
            AStarNode currentNode = openSet.poll();
            Cell current = currentNode.getCell();

            if (closedSet.contains(current)) {
                continue;
            }
            closedSet.add(current);

            explorationOrder.add(current);

            nodesExplored++;

            if (current.equals(goal)) {
                List<Cell> path = reconstructPath(parentMap, goal);

                long executionTime = System.nanoTime() - startTime;
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                long memoryUsed = Math.max(0, memoryAfter - memoryBefore);
                System.out.println("A* zavrsio, duzina puta: " + path.size() + " istraženo čvorova: " + nodesExplored);

                return SearchResult.success(path, getName(), nodesExplored, executionTime, memoryUsed);
            }

            for (Cell neighbor : getNeighbors(maze, current)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                double tentativeG = gScores.get(current) + 1;
                double existingG = gScores.getOrDefault(neighbor, Double.MAX_VALUE);

                if (tentativeG < existingG) {
                    parentMap.put(neighbor, current);
                    gScores.put(neighbor, tentativeG);

                    double heuristicValue = heuristic.calculate(neighbor, goal);

                    openSet.add(new AStarNode(neighbor,tentativeG, heuristicValue));
                }
            }
        }

        long executionTime = System.nanoTime() - startTime;
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.max(0, memoryAfter - memoryBefore);
        System.out.println("A* nije pronašao put");

        return SearchResult.failure(getName(), nodesExplored, executionTime, memoryUsed);
    }

    // Dohvaća dostupne susjede bez zida
    private List<Cell> getNeighbors(Maze maze, Cell cell) {

        List<Cell> neighbors = new ArrayList<>();

        int row = cell.getRow();
        int col = cell.getCol();

        if (!cell.hasWall(Direction.UP) && row > 0) {
            neighbors.add(maze.getCell(row - 1, col));
        }

        if (!cell.hasWall(Direction.DOWN) && row < maze.getRows() - 1) {
            neighbors.add(maze.getCell(row + 1, col));
        }

        if (!cell.hasWall(Direction.LEFT) && col > 0) {
            neighbors.add(maze.getCell(row, col - 1));
        }

        if (!cell.hasWall(Direction.RIGHT) && col < maze.getCols() - 1) {
            neighbors.add(maze.getCell(row, col + 1));
        }

        return neighbors;
    }


    //Rekonstrukcija puta pomoću parentMap.
    private List<Cell> reconstructPath(Map<Cell, Cell> parentMap, Cell goal) {

        List<Cell> path = new ArrayList<>();
        Cell current = goal;

        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path);

        return path;
    }

    public List<Cell> getExplorationOrder() {
        return new ArrayList<>(explorationOrder);
    }

    @Override
    public String getName() {
        return "A* (" + heuristic.getName() + ")";
    }

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.ASTAR;
    }
}
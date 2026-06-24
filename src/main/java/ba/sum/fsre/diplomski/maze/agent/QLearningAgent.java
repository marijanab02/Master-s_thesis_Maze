package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.model.*;
import ba.sum.fsre.diplomski.maze.qlearning.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

// Q-learning agent za autonomnu navigaciju
public class QLearningAgent implements Agent {

    private final QLearningConfig config;

    private final RewardFunction rewardFunction;

    private final EpsilonGreedyPolicy policy;

    private QTable qTable;

    private final TrainingMetrics metrics;

    private List<Cell> bestEpisodePath = new ArrayList<>();

    private double bestReward = Double.NEGATIVE_INFINITY;

    private List<Cell> explorationOrder = new ArrayList<>();

    private final List<EpisodeReplay> savedEpisodes = new ArrayList<>();

    public QLearningAgent(QLearningConfig config) {
        this.config = config;
        this.rewardFunction = new RewardFunction();
        this.policy = new EpsilonGreedyPolicy();
        this.metrics = new TrainingMetrics();
    }

    // Treniranje agenta

    public void train(Maze maze) {
        System.out.println("Pokretanje treninga. Epizode=" + config.getEpisodes());

        qTable = new QTable(maze.getRows(), maze.getCols());

        Cell goal = maze.getGoal();

        for (int episode = 0; episode < config.getEpisodes(); episode++) {

            Cell current = maze.getStart();
            List<Cell> episodePath = new ArrayList<>();
            episodePath.add(current);

            double totalReward = 0;
            int steps = 0;

            while (steps < config.getMaxStepsPerEpisode()) {
                Direction action = policy.chooseAction(qTable, current.getRow(), current.getCol(), config.getEpsilon());

                Cell next = move(maze, current, action);
                boolean validMove = !next.equals(current);
                double reward = rewardFunction.getReward(current, next, goal, validMove);

                qTable.update(current.getRow(), current.getCol(), action, reward,
                        next.getRow(), next.getCol(), config.getAlpha(), config.getGamma()
                );

                current = next;
                episodePath.add(current);
                totalReward += reward;
                steps++;

                if (current.equals(goal)) {
                    break;
                }
            }
            if (episode == 0 || episode == 50 || episode == 100 || episode == 200 || episode == 500)
            {
                savedEpisodes.add(new EpisodeReplay(episode, new ArrayList<>(episodePath), totalReward, steps));
            }
            if (current.equals(goal) && totalReward > bestReward) {
                bestReward = totalReward;
                bestEpisodePath = new ArrayList<>(episodePath);
                explorationOrder = new ArrayList<>(episodePath);
            }
            metrics.addEpisode(totalReward, steps);

            config.decayEpsilon();

            if (episode % 100 == 0) {
                System.out.println("Epizoda: " + episode + " reward: " + totalReward + " koraci: " + steps + " epsilon: " + config.getEpsilon());
            }
        }
        System.out.println("Trening završen.");
    }

    @Override
    public SearchResult solve(Maze maze) {

        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        List<Cell> path = new ArrayList<>();
        Set<Cell> visited = new HashSet<>();

        Cell current = maze.getStart();
        Cell goal = maze.getGoal();
        path.add(current);

        int nodesExplored = 0;

        while (!current.equals(goal) && nodesExplored < 5000) {

            visited.add(current);
            Direction bestAction = qTable.getBestAction(current.getRow(), current.getCol());

            Cell next = move(maze, current, bestAction);

            if (next.equals(current)) {
                break;
            }

            current = next;
            path.add(current);
            nodesExplored++;

            if (visited.contains(current)&& !current.equals(goal)) {
                break;
            }
        }

        long executionTime = System.nanoTime() - startTime;
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.max(0, memoryAfter - memoryBefore);

        if (current.equals(goal)) {
            return SearchResult.success(path, getName(), nodesExplored, executionTime, memoryUsed);
        }

        return SearchResult.failure(getName(), nodesExplored, executionTime, memoryUsed);
    }

    //Pomicanje agenta.
    private Cell move(Maze maze, Cell current, Direction direction) {

        int row = current.getRow();
        int col = current.getCol();

        if (current.hasWall(direction)) {
            return current;
        }

        return switch (direction) {
            case UP ->
                    maze.getCell(row - 1, col);
            case DOWN ->
                    maze.getCell(row + 1, col);
            case LEFT ->
                    maze.getCell(row, col - 1);
            case RIGHT ->
                    maze.getCell(row, col + 1);
        };
    }

    public QTable getQTable() {
        return qTable;
    }

    public TrainingMetrics getMetrics() {
        return metrics;
    }

    @Override
    public String getName() {
        return "Q-Learning";
    }

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.Q_LEARNING;
    }

    public List<Cell> getBestEpisodePath() {
        return new ArrayList<>(bestEpisodePath);
    }

    public List<Cell> getExplorationOrder() {
        return new ArrayList<>(explorationOrder);
    }

    public List<EpisodeReplay> getSavedEpisodes() {
        return savedEpisodes;
    }

}
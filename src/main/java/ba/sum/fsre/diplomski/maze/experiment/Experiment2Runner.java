 package ba.sum.fsre.diplomski.maze.experiment;

import ba.sum.fsre.diplomski.maze.generator.DFSMazeGenerator;
import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Direction;
import ba.sum.fsre.diplomski.maze.model.Maze;
import ba.sum.fsre.diplomski.maze.qlearning.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

// Eksperiment 3: Q-learning konvergencija.
//Za svaku kombinaciju (alpha, gamma, epsilon_decay) pokreni RUNS_PER_CONFIG puta trening, za svaku epizodu u
//svakom runu zabilježi: totalReward, steps, reachedGoal
public class Experiment2Runner {


    // Veličina prozora za rolling average
    private static final int ROLLING_WINDOW = 50;

    private final Maze maze;

    public Experiment2Runner() {
        maze = new DFSMazeGenerator().generate(
                Experiment2Config.MAZE_SIZE,
                Experiment2Config.MAZE_SIZE,
                Experiment2Config.MAZE_SEED
        );
        System.out.println("Labirint generiran: " + Experiment2Config.MAZE_SIZE + "x" + Experiment2Config.MAZE_SIZE + ", seed=" + Experiment2Config.MAZE_SEED);
    }

    public void run() throws IOException {

        ensureResultsDir();

        int totalCombinations = Experiment2Config.ALPHAS.length * Experiment2Config.GAMMAS.length * Experiment2Config.EPSILON_STRATEGIES.length;
        System.out.println("Pokretanje eksperimenta 3");
        System.out.println("Kombinacija: " + totalCombinations);
        System.out.println("Ponavljanja po kombinaciji: " + Experiment2Config.RUNS_PER_CONFIG);
        System.out.println("Epizoda po runu: " + Experiment2Config.EPISODES);
        System.out.println("Ukupno treninga: " + totalCombinations * Experiment2Config.RUNS_PER_CONFIG);

        try (PrintWriter csv = openCsv(Experiment2Config.CSV_FILE)) {
            writeCsvHeader(csv);

            int combinationIndex = 0;

            for (double alpha : Experiment2Config.ALPHAS) {
                for (double gamma : Experiment2Config.GAMMAS) {
                    for (Experiment2Config.EpsilonStrategy strategy : Experiment2Config.EPSILON_STRATEGIES) {

                        combinationIndex++;
                        System.out.println("[" + combinationIndex + "/" + totalCombinations + "] alpha=" + alpha +
                                " gamma=" + gamma + " epsilon-decay=" + strategy.name());

                        runCombination(csv, alpha, gamma, strategy);
                    }
                }
            }
        }
        System.out.println("Eksperiment završen. CSV: " + Experiment2Config.CSV_FILE);
    }
    private void runCombination(PrintWriter csv, double alpha, double gamma, Experiment2Config.EpsilonStrategy strategy) {

        for (int run = 0; run < Experiment2Config.RUNS_PER_CONFIG; run++) {

            System.out.println("Run " + (run+1) + "/" + Experiment2Config.RUNS_PER_CONFIG);

            QTable qTable = new QTable(maze.getRows(), maze.getCols());
            EpsilonGreedyPolicy policy = new EpsilonGreedyPolicy();
            RewardFunction rewardFn = new RewardFunction();

            // Epsilon počinje od 1.0 i opada prema minEpsilon
            double epsilon = 1.0;

            // Buffer za rolling average
            double[] rewardBuffer = new double[Experiment2Config.EPISODES];

            for (int episode = 0; episode < Experiment2Config.EPISODES; episode++) {

                // Pokreni jednu epizodu i vrati metriku
                EpisodeMetrics metrics = runEpisode(qTable, policy, rewardFn, alpha, gamma, epsilon);

                // Decay epsilon
                epsilon = Math.max(strategy.minEpsilon(), epsilon * strategy.decayFactor());

                rewardBuffer[episode] = metrics.totalReward;

                // Izračunaj rolling average
                double rollingAvg = rollingAverage(rewardBuffer, episode, ROLLING_WINDOW);

                // Piši u CSV
                csv.printf(Locale.US, "%s,%s,%s,%d,%d,%.4f,%d,%b,%.4f%n",
                        formatDouble(alpha),
                        formatDouble(gamma),
                        strategy.name(),
                        run,
                        episode,
                        metrics.totalReward,
                        metrics.steps,
                        metrics.reachedGoal,
                        rollingAvg
                );
            }

            csv.flush();
        }
    }

    private EpisodeMetrics runEpisode(QTable qTable, EpsilonGreedyPolicy policy, RewardFunction rewardFn,
            double alpha, double gamma, double epsilon) {
        Cell current = maze.getStart();
        Cell goal    = maze.getGoal();

        double totalReward = 0.0;
        int    steps = 0;
        boolean reachedGoal = false;

        while (steps < Experiment2Config.MAX_STEPS) {

            Direction action = policy.chooseAction(qTable, current.getRow(), current.getCol(), epsilon);

            Cell next = move(current, action);
            boolean validMove = !next.equals(current);

            double reward = rewardFn.getReward(current, next, goal, validMove);

            qTable.update(
                    current.getRow(), current.getCol(),
                    action, reward,
                    next.getRow(), next.getCol(),
                    alpha, gamma
            );

            current = next;
            totalReward += reward;
            steps++;

            if (current.equals(goal)) {
                reachedGoal = true;
                break;
            }
        }

        return new EpisodeMetrics(totalReward, steps, reachedGoal);
    }
    private Cell move(Cell current, Direction direction) {

        if (current.hasWall(direction)) {
            return current;
        }

        int newRow = current.getRow() + direction.getRowOffset();
        int newCol = current.getCol() + direction.getColOffset();

        if (!maze.isInsideBounds(newRow, newCol)) {
            return current;
        }

        return maze.getCell(newRow, newCol);
    }

    private double rollingAverage(double[] buffer, int currentIndex, int window) {
        int start = Math.max(0, currentIndex - window + 1);
        double sum = 0.0;
        for (int i = start; i <= currentIndex; i++) {
            sum += buffer[i];
        }
        return sum / (currentIndex - start + 1);
    }

    private void writeCsvHeader(PrintWriter csv) {
        csv.println("alpha,gamma,epsilonStrategy,run,episode," + "totalReward,steps,reachedGoal,rollingAvgReward");
    }

    private PrintWriter openCsv(String filepath) throws IOException {
        return new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filepath), StandardCharsets.UTF_8
        ));
    }

    private void ensureResultsDir() throws IOException {
        Files.createDirectories(Paths.get(Experiment2Config.RESULTS_DIR));
    }

    private String formatDouble(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((int) value);
        }
        // Ukloni trailing nule: 0.10 → 0.1
        return String.valueOf(value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    // Interni record za metrike jedne epizode
    private record EpisodeMetrics(double  totalReward, int     steps, boolean reachedGoal) {}
}
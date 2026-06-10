package ba.sum.fsre.diplomski.maze.experiment;

import ba.sum.fsre.diplomski.maze.agent.*;
import ba.sum.fsre.diplomski.maze.algorithm.ManhattanHeuristic;
import ba.sum.fsre.diplomski.maze.algorithm.EuclideanHeuristic;
import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.generator.ImperfectMazeGenerator;
import ba.sum.fsre.diplomski.maze.generator.MazeGenerator;
import ba.sum.fsre.diplomski.maze.model.Maze;
import ba.sum.fsre.diplomski.maze.qlearning.QLearningConfig;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//Pokreće Eksperiment 1: Skalabilnost.
//pokreće RUNS_PER_COMBINATION mjerenja i bilježi duljinu puta, broj istraženih čvorova, vrijeme izvođenja, memorijsku potrošnju

//Na kraju exportuje sirove i agregirane rezultate u CSV.
//Q-learning agent se trenira zasebno za svaki labirint jer QTable ovisi o dimenziji, pa se mjerenja odnose
//isključivo na fazu solve() NAKON treninga.
public class Experiment1Runner {

    private final MazeGenerator         generator;
    private final List<Experiment1Result> rawResults;

    public Experiment1Runner() {
        this.generator = new ImperfectMazeGenerator();
        //this.generator  = new DFSMazeGenerator();
        this.rawResults = new ArrayList<>();
    }

    public List<AggregatedResult> run() {

        System.out.println("EKSPERIMENT 1: SKALABILNOST");
        System.out.println("Dimenzije: " + Arrays.toString(Experiment1Config.MAZE_SIZES));
        System.out.println("Ponavljanja: " + Experiment1Config.RUNS_PER_COMBINATION);
        System.out.println("Ukupno mjerenja: " + Experiment1Config.MAZE_SIZES.length * Experiment1Config.RUNS_PER_COMBINATION * 5);

        long experimentStart = System.currentTimeMillis();

        for (int size : Experiment1Config.MAZE_SIZES) {
            System.out.println("Dimenzija labirinta: " + size + "x" + size);
            runForSize(size);

            printSizeProgress(size);
        }

        long totalTime = System.currentTimeMillis() - experimentStart;
        System.out.println("Eksperiment završen za [s]: " + totalTime/1000 );

        // Agregiraj i export
        List<AggregatedResult> aggregated = aggregate();
        exportResults(aggregated);
        printFinalTable(aggregated);

        return aggregated;
    }

    // Jedna dimenzija labirinta
    private void runForSize(int size) {

        for (int run = 0; run < Experiment1Config.RUNS_PER_COMBINATION; run++) {

            // Fiksni seed isti labirint za sve agente u ovom runu
            long seed = Experiment1Config.BASE_SEED + run;
            Maze maze = generator.generate(size, size, seed);

            runAgent(new DFSAgent(),  maze, size, run, seed);
            runAgent(new BFSAgent(),  maze, size, run, seed);
            runAgent(new AStarAgent(new ManhattanHeuristic()), maze, size, run, seed);
            runAgent(new AStarAgent(new EuclideanHeuristic()), maze, size, run, seed);

            //Q-learning: treniraj pa mjeri solve()
            runQLearning(maze, size, run, seed);

            if ((run+1) % 10 == 0) {
                System.out.println("Run " + (run+1) + " završen");
            }
        }
    }
    // Pokretanje jednog  agenta
    private void runAgent(Agent agent, Maze maze, int size, int run, long seed) {
        try {
            SearchResult result = agent.solve(maze);

            rawResults.add(new Experiment1Result(
                    agent.getName(),
                    size, run, seed,
                    result.isPathFound(),
                    result.getPathLength(),
                    result.getNodesExplored(),
                    result.getExecutionTimeNs(),
                    result.getMemoryUsedBytes()
            ));

        } catch (Exception e) {
            System.out.print("Greška agenta: " + agent.getName() + " " + e.getMessage());
            // Zabilježi neuspjeh
            rawResults.add(new Experiment1Result(
                    agent.getName(), size, run, seed,
                    false, 0, 0, 0L, 0L
            ));
        }
    }

    // Q-learning: treniraj zasebno, mjeri samo solve()
    private void runQLearning(Maze maze, int size, int run, long seed) {

        try {
            QLearningConfig config = new QLearningConfig(
                    0.1,
                    0.95,
                    1.0,
                    0.995,
                    0.01,
                    size * size * 10,  // maxStepsPerEpisode, proporcionalno dimenziji
                    Experiment1Config.Q_LEARNING_TRAIN_EPISODES
            );

            QLearningAgent agent = new QLearningAgent(config);

            // Treniraj NE mjeri se u eksperimentu (zasebna analiza)
            agent.train(maze);

            // Mjeri samo solve()
            SearchResult result = agent.solve(maze);

            rawResults.add(new Experiment1Result(
                    "Q-Learning",
                    size, run, seed,
                    result.isPathFound(),
                    result.getPathLength(),
                    result.getNodesExplored(),
                    result.getExecutionTimeNs(),
                    result.getMemoryUsedBytes()
            ));

        } catch (Exception e) {
            System.out.println("Greška Q-learninga");

            rawResults.add(new Experiment1Result("Q-Learning", size, run, seed,
                    false, 0, 0, 0L, 0L
            ));
        }
    }

    // Agregacija
    private List<AggregatedResult> aggregate() {

        // Grupiraj po (algorithmName, mazeSize)
        Map<String, List<Experiment1Result>> grouped = rawResults.stream()
                .collect(Collectors.groupingBy(
                        r -> r.algorithmName() + "|" + r.mazeSize()
                ));

        List<AggregatedResult> aggregated = new ArrayList<>();

        for (int size : Experiment1Config.MAZE_SIZES) {
            for (String algoName : getAlgorithmOrder()) {

                String key = algoName + "|" + size;
                List<Experiment1Result> group = grouped.getOrDefault(key, List.of());

                if (!group.isEmpty()) {
                    aggregated.add(AggregatedResult.from(algoName, size, group));
                }
            }
        }

        return aggregated;
    }

    private List<String> getAlgorithmOrder() {
        return List.of(
                "Depth-First Search",
                "Breadth-First Search",
                "A* (Manhattan)",
                "A* (Euclidean)",
                "Q-Learning"
        );
    }

    // Export
    private void exportResults(List<AggregatedResult> aggregated) {
        try {
            CsvExporter.exportRaw(rawResults, Experiment1Config.RAW_CSV_FILE);
            CsvExporter.exportAggregated(aggregated, Experiment1Config.AGGREGATED_CSV_FILE);
            System.out.println("Rezultati spremljeni u: " + Experiment1Config.RESULTS_DIR);
        } catch (IOException e) {
            System.out.println("CSV export nije uspio");
        }
    }

    // Ispis u konzolu
    private void printSizeProgress(int size) {

        System.out.printf("%n  [%d×%d] Završeno.%n", size, size);

        rawResults.stream()
                .filter(r -> r.mazeSize() == size)
                .collect(Collectors.groupingBy(Experiment1Result::algorithmName))
                .forEach((name, results) -> {
                    long successes = results.stream().filter(Experiment1Result::pathFound).count();
                    double avgTime = results.stream()
                            .mapToDouble(Experiment1Result::executionTimeMs).average().orElse(0);
                    System.out.printf(" %s | uspjeh=%d/%d | avg_time=%.2f ms%n",
                            name, successes, results.size(), avgTime);
                });
    }

    private void printFinalTable(List<AggregatedResult> aggregated) {

        System.out.println("EKSPERIMENT 1");
        System.out.println("Algoritam | N | Uspjeh | Put (avg±std) | Čvorovi (avg) | Vrijeme(ms)");
        System.out.println("-".repeat(90));

        for (AggregatedResult r : aggregated) {
            System.out.printf("%s | %d | %.0f%% | %.1f ± %.1f | %.0f | %.3f%n",
                    r.algorithmName, r.mazeSize,
                    r.successRate * 100,
                    r.avgPathLength, r.stdPathLength,
                    r.avgNodesExplored,
                    r.avgExecutionTimeMs);
        }

        System.out.println("CSV rezultati: " + Experiment1Config.RESULTS_DIR);
    }

    public List<Experiment1Result> getRawResults() {
        return Collections.unmodifiableList(rawResults);
    }
}
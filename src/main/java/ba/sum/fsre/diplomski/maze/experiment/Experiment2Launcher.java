package ba.sum.fsre.diplomski.maze.experiment;

public class Experiment2Launcher {

    public static void main(String[] args) {
        System.out.println("Eksperiment 3: Q_learning konvergencija");
        System.out.println("Labirint: " + Experiment2Config.MAZE_SIZE + "x" + Experiment2Config.MAZE_SIZE + " seed=" + Experiment2Config.MAZE_SEED);
        System.out.println("Kombinacije: alpha x gamma x epsilon = " + Experiment2Config.ALPHAS.length + "x" + Experiment2Config.GAMMAS.length + "x" +
                Experiment2Config.EPSILON_STRATEGIES.length + " = " + Experiment2Config.ALPHAS.length * Experiment2Config.GAMMAS.length *
                Experiment2Config.EPSILON_STRATEGIES.length);
        System.out.println("Epizoda: " + Experiment2Config.EPISODES);
        System.out.print("Runova: " + Experiment2Config.RUNS_PER_CONFIG);

        long start = System.currentTimeMillis();

        try {
            Experiment2Runner runner = new Experiment2Runner();
            runner.run();

            long elapsed = (System.currentTimeMillis()-start) / 1000;

            System.out.println("Trajanje u s: " + elapsed);
            System.out.println("CSV: " + Experiment2Config.CSV_FILE);

        } catch (Exception e) {
            System.out.println("Eksperiment nije uspio");
            System.exit(1);
        }
    }
}
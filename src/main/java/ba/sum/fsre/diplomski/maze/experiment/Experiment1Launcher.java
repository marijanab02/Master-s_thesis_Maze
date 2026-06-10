package ba.sum.fsre.diplomski.maze.experiment;

import java.util.List;

//Eksperiment 1: Skalabilnost
public class Experiment1Launcher {

    public static void main(String[] args) {
        System.out.println("Pokretanje Eksperimenta 1: Skalabilnost");

        System.out.println("Konfiguracija");

        System.out.println("Konfiguracija: " + java.util.Arrays.toString(Experiment1Config.MAZE_SIZES));
        System.out.println("Ponavljanja po kombinaciji: " + Experiment1Config.RUNS_PER_COMBINATION);

        System.out.println("Q-learning epizode: " + Experiment1Config.Q_LEARNING_TRAIN_EPISODES);

        Experiment1Runner runner = new Experiment1Runner();

        List<AggregatedResult> results = runner.run();

        System.out.println("Eksperiment 1 završen");
    }
}
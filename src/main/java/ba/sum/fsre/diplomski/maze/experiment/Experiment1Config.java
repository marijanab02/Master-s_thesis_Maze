package ba.sum.fsre.diplomski.maze.experiment;


public class Experiment1Config {

    public static final int[] MAZE_SIZES = {10, 20, 30, 50, 100};


     //Broj ponavljanja po (algoritam, dimenzija) kombinaciji.
    public static final int RUNS_PER_COMBINATION = 10;

    //Bazni seed za generator.
    public static final long BASE_SEED = 42L;

    public static final String RESULTS_DIR = "results";

    public static final String RAW_CSV_FILE = RESULTS_DIR + "/experiment1_raw.csv";

    public static final String AGGREGATED_CSV_FILE = RESULTS_DIR + "/experiment1_aggregated.csv";

    public static final int Q_LEARNING_TRAIN_EPISODES = 5000;

    //Maksimalni timeout po jednom mjerenju (ms).
    public static final long TIMEOUT_MS = 30_000;

    private Experiment1Config() {}
}
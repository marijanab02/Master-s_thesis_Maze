package ba.sum.fsre.diplomski.maze.experiment;

//Konfiguracija Eksperimenta 3: Q-learning konvergencija
//Testira sve kombinacije: α = {0.1, 0.3, 0.5, 0.7}, γ = {0.8, 0.9, 0.99} ε-decay {FAST, MEDIUM, SLOW}
//Ukupno: 4 × 3 × 3 = 36 kombinacija
public class Experiment2Config {

    // Dimenzija labirinta
    public static final int MAZE_SIZE = 10;

    //Svi testovi na istom labirintu
    public static final long MAZE_SEED = 42L;

    //Broj epizoda treninga
    public static final int EPISODES = 1000;

    // Broj koraka po epizodi
    public static final int MAX_STEPS = 1000;

    // Broj ponavljanja po kombinaciji
    public static final int RUNS_PER_CONFIG = 5;

    // Alpha vrijednosti
    public static final double[] ALPHAS = {0.1, 0.3, 0.5, 0.7};

    // Gamma vrijednosti
    public static final double[] GAMMAS = {0.8, 0.9, 0.99};

    // Epsilon-decay strategije: (naziv, decay_factor, min_epsilon)
    public static final EpsilonStrategy[] EPSILON_STRATEGIES = {
            new EpsilonStrategy("Brzi decay (0.990)",  0.990, 0.01),
            new EpsilonStrategy("Srednji decay (0.995)", 0.995, 0.01),
            new EpsilonStrategy("Spori decay (0.999)",  0.999, 0.01),
    };

    public static final String RESULTS_DIR = "results";
    public static final String CSV_FILE = RESULTS_DIR + "/experiment2_1000ep_raw.csv";

    private Experiment2Config() {}

    //Enkapsulira jednu epsilon-decay strategiju
    public record EpsilonStrategy(
            String name,
            double decayFactor,
            double minEpsilon
    ) {}
}
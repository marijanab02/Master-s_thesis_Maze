package ba.sum.fsre.diplomski.maze.qlearning;

//Konfiguracija hiperparametara za Q-learning agenta
public class QLearningConfig {

    //Learning rate (α)
    private final double alpha;

    //Discount factor (γ)
    private final double gamma;

    //Exploration rate (epsilon)
    private double epsilon;

    //Faktor smanjenja epsilon vrijednosti.
    private final double epsilonDecay;

    //Minimalni epsilon
    private final double minEpsilon;

    // Maksimalan broj koraka po epizodi
    private final int maxStepsPerEpisode;

    //Broj trening epizoda
    private final int episodes;

    public QLearningConfig(double alpha, double gamma, double epsilon, double epsilonDecay, double minEpsilon, int maxStepsPerEpisode, int episodes) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.epsilonDecay = epsilonDecay;
        this.minEpsilon = minEpsilon;
        this.maxStepsPerEpisode = maxStepsPerEpisode;
        this.episodes = episodes;
    }

    public static QLearningConfig defaultConfig() {

        return new QLearningConfig(0.4, 0.95, 1.0, 0.995, 0.01, 1000, 5000);
    }

    public void decayEpsilon() {
        epsilon = Math.max(minEpsilon, epsilon * epsilonDecay);
    }

    public double getAlpha() {
        return alpha;
    }

    public double getGamma() {
        return gamma;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double getEpsilonDecay() {
        return epsilonDecay;
    }

    public double getMinEpsilon() {
        return minEpsilon;
    }

    public int getMaxStepsPerEpisode() {
        return maxStepsPerEpisode;
    }

    public int getEpisodes() {
        return episodes;
    }
}
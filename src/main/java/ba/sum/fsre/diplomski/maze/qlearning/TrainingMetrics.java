package ba.sum.fsre.diplomski.maze.qlearning;

import java.util.ArrayList;
import java.util.List;

// Praćenje trening metrika
public class TrainingMetrics {

    private final List<Double> rewardsPerEpisode;

    private final List<Integer> stepsPerEpisode;

    public TrainingMetrics() {
        this.rewardsPerEpisode = new ArrayList<>();
        this.stepsPerEpisode = new ArrayList<>();
    }

    public void addEpisode(double reward, int steps) {
        rewardsPerEpisode.add(reward);
        stepsPerEpisode.add(steps);
    }

    public List<Double> getRewardsPerEpisode() {
        return rewardsPerEpisode;
    }

    public List<Integer> getStepsPerEpisode() {
        return stepsPerEpisode;
    }
}
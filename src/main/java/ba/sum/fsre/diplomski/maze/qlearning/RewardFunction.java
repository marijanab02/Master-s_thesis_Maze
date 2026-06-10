package ba.sum.fsre.diplomski.maze.qlearning;

import ba.sum.fsre.diplomski.maze.model.Cell;

// Funkcija nagrade za Q-learning.
public class RewardFunction {

    // Nagrada za dolazak do cilja.
    private static final double GOAL_REWARD = 100.0;

    //Kazna za svaki korak.
    private static final double STEP_PENALTY = -1.0;

    // Kazna za udar u zid
    private static final double WALL_PENALTY = -5.0;

    public double getReward(Cell current, Cell next, Cell goal, boolean validMove) {
        if (!validMove) {
            return WALL_PENALTY;
        }
        if (next.equals(goal)) {
            return GOAL_REWARD;
        }
        return STEP_PENALTY;
    }
}
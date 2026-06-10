package ba.sum.fsre.diplomski.maze.qlearning;

import ba.sum.fsre.diplomski.maze.model.Direction;

import java.util.Random;

// Epsilon-greedy strategija odabira akcija.
public class EpsilonGreedyPolicy {

    private final Random random = new Random();

    public Direction chooseAction(QTable qTable, int row, int col, double epsilon) {

        if (random.nextDouble() < epsilon) {
            Direction[] directions = Direction.values();

            return directions[random.nextInt(directions.length)];
        }

        return qTable.getBestAction(row, col);
    }
}
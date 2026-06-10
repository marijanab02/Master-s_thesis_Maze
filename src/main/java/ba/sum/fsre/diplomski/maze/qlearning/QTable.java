package ba.sum.fsre.diplomski.maze.qlearning;

import ba.sum.fsre.diplomski.maze.model.Direction;

import java.util.Arrays;

//Q-tabela: [row][col][action]
public class QTable {

    private final double[][][] table;

    public QTable(int rows, int cols) {
        this.table = new double[rows][cols][Direction.values().length];
    }

    public double getValue(int row, int col, Direction action) {
        return table[row][col][action.ordinal()];
    }

    public void setValue(int row, int col, Direction action, double value) {
        table[row][col][action.ordinal()] = value;
    }

    public double getMaxQValue(int row, int col) {
        return Arrays.stream(table[row][col])
                .max()
                .orElse(0.0);
    }

    public Direction getBestAction(int row, int col) {
        double max = Double.NEGATIVE_INFINITY;
        Direction best = Direction.UP;

        for (Direction direction : Direction.values()) {
            double q =
                    table[row][col][direction.ordinal()];

            if (q>max) {
                max = q;
                best = direction;
            }
        }
        return best;
    }

    public void update(int row, int col, Direction action, double reward, int newRow, int newCol, double alpha, double gamma) {

        double currentQ = getValue(row, col, action);
        double maxFutureQ = getMaxQValue(newRow, newCol);

        double updatedQ = currentQ + alpha*(reward + gamma*maxFutureQ - currentQ);

        setValue(row, col, action, updatedQ);
    }

    public double[][][] getTable() {
        return table;
    }
}
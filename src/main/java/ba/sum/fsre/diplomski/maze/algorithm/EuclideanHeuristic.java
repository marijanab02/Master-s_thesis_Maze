package ba.sum.fsre.diplomski.maze.algorithm;

import ba.sum.fsre.diplomski.maze.model.Cell;

// Euklidova heuristika koristi zračnu udaljenost između ćelija
public class EuclideanHeuristic implements Heuristic {

    @Override
    public double calculate(Cell current, Cell goal) {

        int dx = current.getRow() - goal.getRow();
        int dy = current.getCol() - goal.getCol();

        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String getName() {
        return "Euclidean";
    }
}
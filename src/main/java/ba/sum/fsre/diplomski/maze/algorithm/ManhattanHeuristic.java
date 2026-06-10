package ba.sum.fsre.diplomski.maze.algorithm;

import ba.sum.fsre.diplomski.maze.model.Cell;

//Manhattan heuristika
//Koristi Manhattan udaljenost: |x1-x2| + |y1-y2|
//Optimalna za grid kretanje gore/dolje/lijevo/desno.
public class ManhattanHeuristic implements Heuristic {

    @Override
    public double calculate(Cell current, Cell goal) {
        return Math.abs(current.getRow()-goal.getRow()) + Math.abs(current.getCol()-goal.getCol());
    }

    @Override
    public String getName() {
        return "Manhattan";
    }
}
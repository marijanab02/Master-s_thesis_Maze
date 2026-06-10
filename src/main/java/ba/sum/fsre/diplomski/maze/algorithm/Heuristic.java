package ba.sum.fsre.diplomski.maze.algorithm;

import ba.sum.fsre.diplomski.maze.model.Cell;


// Interface za heurističke funkcije korištene u A* algoritmu
//Heuristika procjenjuje udaljenost između trenutne ćelije i cilja.
public interface Heuristic {

    // Procjenjuje udaljenost između dvije ćelije
    double calculate(Cell current, Cell goal);

    String getName();
}
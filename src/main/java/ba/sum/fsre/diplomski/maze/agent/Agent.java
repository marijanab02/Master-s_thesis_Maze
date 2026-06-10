package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.model.Maze;

//Zajednički interface za sve agente navigacije
public interface Agent {

    //Pokreće algoritam navigacije koristeći start i goal definirane u labirintu.
    SearchResult solve(Maze maze);

    String getName();

    AlgorithmType getType();
}
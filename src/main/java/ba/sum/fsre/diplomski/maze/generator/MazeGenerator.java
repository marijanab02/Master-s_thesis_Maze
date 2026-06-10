package ba.sum.fsre.diplomski.maze.generator;


import ba.sum.fsre.diplomski.maze.model.Maze;

//Sučelje za sve algoritme generiranja labirinta
public interface MazeGenerator {

    Maze generate(int rows, int cols);

    //Generira novi labirint koristeći zadani seed - za generiranje slučajnih brojeva
    Maze generate(int rows, int cols, long seed);

    String getName();
}

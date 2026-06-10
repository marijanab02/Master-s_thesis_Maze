package ba.sum.fsre.diplomski.maze.generator;

import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Direction;
import ba.sum.fsre.diplomski.maze.model.Maze;

import java.util.Random;

// Generator nesavršenih labirinata (imperfect maze).
// Strategija: Generira savršeni labirint pomoću DFSMazeGenerator-a, nasumično uklanja dodatne zidove između susjednih ćelija.

public class ImperfectMazeGenerator implements MazeGenerator {

     //Vjerojatnost dodavanja dodatnog prolaza.
    private double extraPassageProbability;

    //Broj dodatno otvorenih prolaza, približno odgovara broju stvorenih ciklusa.
    private int numberOfCycles;

    public ImperfectMazeGenerator() {
        this(0.20);
    }

    public ImperfectMazeGenerator(double extraPassageProbability) {
        if (extraPassageProbability < 0.0 || extraPassageProbability > 1.0) {
            throw new IllegalArgumentException("Vjerojatnost mora biti između 0 i 1.");
        }

        this.extraPassageProbability = extraPassageProbability;
    }

    @Override
    public Maze generate(int rows, int cols) {

        long seed = System.nanoTime();

        return generate(rows, cols, seed);
    }

    @Override
    public Maze generate(int rows, int cols, long seed) {
        System.out.println("Generiranje nesavršenog labirinta: " + rows + "x" + cols + ", seed=" + seed + "p=" + extraPassageProbability);

        Random random = new Random(seed);

        //Generiraj savršeni labirint
        MazeGenerator dfsGenerator = new DFSMazeGenerator();

        Maze maze = dfsGenerator.generate(rows, cols, seed);

        // Dodaj dodatne rolaze
        numberOfCycles = addExtraPassages(maze, random);

        maze.setStart(maze.getCell(0, 0));
        maze.setGoal(maze.getCell(rows - 1, cols - 1));

        System.out.println("Nesavršeni labirint generiran. Dodano prolaza: " + numberOfCycles);

        return maze;
    }

    private int addExtraPassages(Maze maze, Random random) {

        int cyclesCreated = 0;

        int rows = maze.getRows();
        int cols = maze.getCols();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                Cell current = maze.getCell(row, col);

                cyclesCreated += tryRemoveWall(maze, current, Direction.RIGHT, random);
                cyclesCreated += tryRemoveWall(maze, current, Direction.DOWN, random);
            }
        }

        return cyclesCreated;
    }

    private int tryRemoveWall(Maze maze, Cell current, Direction direction, Random random) {
        int newRow = current.getRow() + direction.getRowOffset();
        int newCol = current.getCol() + direction.getColOffset();

        if (!maze.isInsideBounds(newRow, newCol)) {
            return 0;
        }

        if (!current.hasWall(direction)) {
            return 0;
        }

        if (random.nextDouble() > extraPassageProbability) {
            return 0;
        }

        Cell neighbor = maze.getCell(newRow, newCol);
        removeWalls(current, neighbor, direction);

        return 1;
    }

    //Simetrično uklanjanje zidova
    private void removeWalls(Cell current, Cell neighbor, Direction direction) {
        current.removeWall(direction);

        neighbor.removeWall(direction.opposite());
    }

    public int getNumberOfCycles() {
        return numberOfCycles;
    }

    public double getExtraPassageProbability() {
        return extraPassageProbability;
    }

    public void setExtraPassageProbability(double extraPassageProbability) {

        if (extraPassageProbability < 0.0 || extraPassageProbability > 1.0) {

            throw new IllegalArgumentException("Vjerojatnost mora biti između 0 i 1.");
        }

        this.extraPassageProbability = extraPassageProbability;
    }

    @Override
    public String getName() {
        return "Imperfect DFS Maze";
    }
}
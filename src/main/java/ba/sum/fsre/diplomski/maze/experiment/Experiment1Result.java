package ba.sum.fsre.diplomski.maze.experiment;

public record Experiment1Result(

        String  algorithmName,
        int mazeSize,        // N gdje je labirint N×N
        int runIndex,        // 0..29 (redni broj ponavljanja)
        long seed,            // seed kojim je generiran labirint
        boolean pathFound,
        int pathLength,
        int nodesExplored,
        long executionTimeNs,
        long memoryUsedBytes

) {

    // Pomoćna metoda, čita execution time u ms za ispis.
    public double executionTimeMs() {
        return executionTimeNs / 1_000_000.0;
    }

    //Vraca omjer puta: pathLength / nodesExplored
    public double pathEfficiency() {
        if (nodesExplored == 0) return 0.0;
        return (double) pathLength / nodesExplored;
    }
}
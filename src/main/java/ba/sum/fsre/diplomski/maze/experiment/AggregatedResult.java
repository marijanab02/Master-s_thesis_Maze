package ba.sum.fsre.diplomski.maze.experiment;

import java.util.List;


// Agregirana statistika, sadrži srednju vrijednost, devijaciju za sve primarne metrike.
public class AggregatedResult {

    public final String algorithmName;
    public final int    mazeSize;
    public final int    totalRuns;
    public final int    successfulRuns;

    // Metrike-srednja vrijednost
    public final double avgPathLength;
    public final double avgNodesExplored;
    public final double avgExecutionTimeMs;
    public final double avgMemoryBytes;
    public final double avgPathEfficiency;

    // Metrike-standardna devijacija
    public final double stdPathLength;
    public final double stdNodesExplored;
    public final double stdExecutionTimeMs;

    // Stopa uspjeha (0.0–1.0)
    public final double successRate;

    private AggregatedResult(Builder b) {
        this.algorithmName = b.algorithmName;
        this.mazeSize = b.mazeSize;
        this.totalRuns = b.totalRuns;
        this.successfulRuns = b.successfulRuns;
        this.avgPathLength = b.avgPathLength;
        this.avgNodesExplored = b.avgNodesExplored;
        this.avgExecutionTimeMs = b.avgExecutionTimeMs;
        this.avgMemoryBytes = b.avgMemoryBytes;
        this.avgPathEfficiency = b.avgPathEfficiency;
        this.stdPathLength = b.stdPathLength;
        this.stdNodesExplored = b.stdNodesExplored;
        this.stdExecutionTimeMs = b.stdExecutionTimeMs;
        this.successRate = b.successRate;
    }

    public static AggregatedResult from(String algorithmName, int mazeSize, List<Experiment1Result> results) {
        Builder b = new Builder();
        b.algorithmName = algorithmName;
        b.mazeSize = mazeSize;
        b.totalRuns = results.size();

        List<Experiment1Result> ok = results.stream()
                .filter(Experiment1Result::pathFound)
                .toList();

        b.successfulRuns = ok.size();
        b.successRate = results.isEmpty() ? 0.0 : (double) ok.size() / results.size();

        if (ok.isEmpty()) return new AggregatedResult(b);

        // Srednje vrijednosti
        b.avgPathLength = mean(ok.stream().mapToDouble(Experiment1Result::pathLength).toArray());
        b.avgNodesExplored = mean(ok.stream().mapToDouble(Experiment1Result::nodesExplored).toArray());
        b.avgExecutionTimeMs = mean(ok.stream().mapToDouble(Experiment1Result::executionTimeMs).toArray());
        b.avgMemoryBytes = mean(ok.stream().mapToDouble(r -> (double) r.memoryUsedBytes()).toArray());
        b.avgPathEfficiency = mean(ok.stream().mapToDouble(Experiment1Result::pathEfficiency).toArray());

        // Standardna devijacija
        b.stdPathLength = std(ok.stream().mapToDouble(Experiment1Result::pathLength).toArray());
        b.stdNodesExplored = std(ok.stream().mapToDouble(Experiment1Result::nodesExplored).toArray());
        b.stdExecutionTimeMs = std(ok.stream().mapToDouble(Experiment1Result::executionTimeMs).toArray());

        return new AggregatedResult(b);
    }

    private static double mean(double[] values) {
        if (values.length == 0) return 0.0;
        double sum = 0;
        for (double v : values) sum += v;
        return sum/values.length;
    }

    private static double std(double[] values) {
        if (values.length < 2)
            return 0.0;
        double m = mean(values);
        double sumSq = 0;
        for (double v : values) sumSq += (v - m)*(v - m);
        return Math.sqrt(sumSq / (values.length - 1));
    }

    @Override
    public String toString() {
        return String.format(
                "%s | size=%d | sr=%.0f%% | pathLen=%.1f±%.1f | nodes=%.0f±%.0f | time=%.2f ms",
                algorithmName, mazeSize, successRate * 100, avgPathLength, stdPathLength,
                avgNodesExplored, stdNodesExplored, avgExecutionTimeMs);
    }

    private static class Builder {
        String algorithmName;
        int mazeSize, totalRuns, successfulRuns;
        double avgPathLength, avgNodesExplored, avgExecutionTimeMs, avgMemoryBytes, avgPathEfficiency, successRate;
        double stdPathLength, stdNodesExplored, stdExecutionTimeMs;
    }
}
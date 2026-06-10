package ba.sum.fsre.diplomski.maze.experiment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Locale;

// Izvozi rezultate eksperimenta u CSV formate.
public class CsvExporter {

    public static void exportRaw(List<Experiment1Result> results, String filepath) throws IOException {

        ensureDirectory(filepath);

        try (PrintWriter w = openWriter(filepath)) {

            w.println("algorithmName,mazeSize,runIndex,seed," +
                            "pathFound,pathLength,nodesExplored," +
                            "executionTimeNs,executionTimeMs,memoryUsedBytes,pathEfficiency");

            for (Experiment1Result r : results) {
                w.printf(Locale.US, "%s,%d,%d,%d,%b,%d,%d,%d,%.4f,%d,%.4f%n",
                        csvEscape(r.algorithmName()),
                        r.mazeSize(),
                        r.runIndex(),
                        r.seed(),
                        r.pathFound(),
                        r.pathLength(),
                        r.nodesExplored(),
                        r.executionTimeNs(),
                        r.executionTimeMs(),
                        r.memoryUsedBytes(),
                        r.pathEfficiency()
                );
            }
        }
        System.out.println("Reultati spremljeni");
    }

    public static void exportAggregated(List<AggregatedResult> results, String filepath) throws IOException {

        ensureDirectory(filepath);

        try (PrintWriter w = openWriter(filepath)) {

            w.println("algorithmName,mazeSize,totalRuns,successfulRuns,successRate," +
                            "avgPathLength,stdPathLength," + "avgNodesExplored,stdNodesExplored," +
                            "avgExecutionTimeMs,stdExecutionTimeMs," + "avgMemoryBytes,avgPathEfficiency");

            for (AggregatedResult r : results) {
                w.printf(Locale.US,
                        "%s,%d,%d,%d,%.4f," + "%.2f,%.2f," +
                                "%.2f,%.2f," + "%.4f,%.4f," +
                                "%.2f,%.4f%n",
                        csvEscape(r.algorithmName),
                        r.mazeSize,
                        r.totalRuns,
                        r.successfulRuns,
                        r.successRate,
                        r.avgPathLength,    r.stdPathLength,
                        r.avgNodesExplored, r.stdNodesExplored,
                        r.avgExecutionTimeMs, r.stdExecutionTimeMs,
                        r.avgMemoryBytes,
                        r.avgPathEfficiency
                );
            }
        }
        System.out.println("Reultati spremljeni");
    }
    private static PrintWriter openWriter(String filepath) throws IOException {
        return new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filepath), StandardCharsets.UTF_8));
    }

    private static void ensureDirectory(String filepath) throws IOException {
        Path dir = Paths.get(filepath).getParent();
        if (dir != null) Files.createDirectories(dir);
    }

    private static String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
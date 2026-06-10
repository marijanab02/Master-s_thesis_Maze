package ba.sum.fsre.diplomski.maze.algorithm;

import ba.sum.fsre.diplomski.maze.model.Cell;

import java.util.List;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// PRikaz rezultata izvršavanja algoritma pretrage
public class SearchResult {

    // Pronađeni put od starta do cilja.
    private final List<Cell> path;

    private final String name;

    // Broj istraženih čvorova tijekom pretrage.
    private final int nodesExplored;

    // Vrijeme izvođenja u nanosekundama
    private final long executionTimeNs;

    // Procijenjena memorijska potrošnja u bajtovima
    private final long memoryUsedBytes;

    private final boolean pathFound;

    private SearchResult(List<Cell> path, String name, int nodesExplored, long executionTimeNs, long memoryUsedBytes, boolean pathFound) {

        this.path = path != null
                ? Collections.unmodifiableList(path)
                : null;

        this.name = name;
        this.nodesExplored = nodesExplored;
        this.executionTimeNs = executionTimeNs;
        this.memoryUsedBytes = memoryUsedBytes;
        this.pathFound = pathFound;
    }

    // Kreira uspješan rezultat pretrage
    public static SearchResult success(List<Cell> path, String name, int nodesExplored, long executionTimeNs, long memoryUsedBytes) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException(
                    "Put ne smije biti null ili prazan za uspješan rezultat"
            );
        }

        return new SearchResult(path, name, nodesExplored, executionTimeNs, memoryUsedBytes, true);
    }

    //Kreira neuspješan rezultat pretrage.
    public static SearchResult failure(String name, int nodesExplored,long executionTimeNs,long memoryUsedBytes) {

        return new SearchResult(null, name,  nodesExplored, executionTimeNs, memoryUsedBytes, false);
    }

    //Getteri i setteri
    public List<Cell> getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public int getNodesExplored() { return nodesExplored;}

    public long getExecutionTimeNs() {
        return executionTimeNs;
    }

    public long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }

    public boolean isPathFound() {
        return pathFound;
    }

    public int getPathLength() {
        return path != null ? path.size() : 0;
    }

    //Vraća omjer efikasnosti pretrage, pathLength / nodesExplored
    public double getPathEfficiency() {

        if (nodesExplored == 0) {
            return 0.0;
        }
        return (double) getPathLength() / nodesExplored;
    }

    //Konvertira rezultat u mapu pogodnu za CSV
    public Map<String, Object> toMap() {

        Map<String, Object> map = new HashMap<>();

        map.put("pathFound", pathFound);
        map.put("pathLength", getPathLength());
        map.put("nodesExplored", nodesExplored);
        map.put("executionTimeNs", executionTimeNs);
        map.put("memoryUsedBytes", memoryUsedBytes);
        map.put("pathEfficiency", getPathEfficiency());

        return map;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "name=" + name +
                "pathFound=" + pathFound +
                ", pathLength=" + getPathLength() +
                ", nodesExplored=" + nodesExplored +
                ", executionTimeNs=" + executionTimeNs +
                ", memoryUsedBytes=" + memoryUsedBytes +
                '}';
    }
}
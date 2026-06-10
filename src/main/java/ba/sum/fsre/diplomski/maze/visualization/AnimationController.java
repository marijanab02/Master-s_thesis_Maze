package ba.sum.fsre.diplomski.maze.visualization;

import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.model.Cell;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Kontroler za animaciju kretanja agenta
public class AnimationController {

    private final MazePanel mazePanel;
    private Timer timer;
    private List<Cell> explorationOrder;
    private List<Cell> finalPath;
    private int currentStep;
    private final Set<Cell> visitedCells;


    public AnimationController(MazePanel mazePanel) {
        this.mazePanel = mazePanel;
        this.visitedCells = new HashSet<>();
    }

    // Pokreće animaciju, exploarationOrder-redoslijed istraživanja, delayMs-pauza imeđu koraka
    public void startAnimation(List<Cell> explorationOrder, SearchResult result, int delayMs) {

        this.explorationOrder = explorationOrder;
        this.finalPath = result.getPath();
        this.currentStep = 0;
        visitedCells.clear();
        timer = new Timer(delayMs, e -> {

            if (currentStep < explorationOrder.size()) {
                Cell current = explorationOrder.get(currentStep);
                visitedCells.add(current);
                mazePanel.setVisitedCells(visitedCells);
                mazePanel.setAgentPosition(current);
                currentStep++;
            } else {
                timer.stop();
                mazePanel.setPath(finalPath);
                showStatistics(result);
            }
        });

        timer.start();
    }

    // Ispis završne statistike
    private void showStatistics(SearchResult result) {

        System.out.println("\nSTATISTIKA:");
        System.out.println(result.getName());
        System.out.println("Ukupno istraženih čvorova: " + result.getNodesExplored());
        System.out.println("Dužina puta: " + result.getPathLength());
        System.out.println("Vrijeme izvođenja (ms): " + result.getExecutionTimeNs() / 1_000_000.0);
        System.out.println("Memorija (bytes): " + result.getMemoryUsedBytes());
        System.out.println();
    }
}
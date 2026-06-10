package ba.sum.fsre.diplomski.maze.agent;

import ba.sum.fsre.diplomski.maze.model.Cell;


//Pomoćna klasa za A* algoritam
//Sprema: ćeliju, g cost (udaljenost od starta), h cost (heuristika), f cost = g + h
public class AStarNode implements Comparable<AStarNode> {

    private final Cell cell;

    private final double gCost;
    private final double hCost;
    private final double fCost;

    public AStarNode(Cell cell, double gCost, double hCost) {
        this.cell = cell;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    public Cell getCell() {
        return cell;
    }

    public double getGCost() {
        return gCost;
    }

    public double getHCost() {
        return hCost;
    }

    public double getFCost() {
        return fCost;
    }

    @Override
    public int compareTo(AStarNode other) {
        return Double.compare(this.fCost, other.fCost);
    }
}
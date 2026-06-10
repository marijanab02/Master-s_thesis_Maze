package ba.sum.fsre.diplomski.maze.visualization;

import ba.sum.fsre.diplomski.maze.model.Cell;
import ba.sum.fsre.diplomski.maze.model.Direction;
import ba.sum.fsre.diplomski.maze.model.Maze;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// Swing panel za vizualizaciju labirinta, puta, posjećenih ćelija i trenutne pozicije agenta
public class MazePanel extends JPanel {

    //Boje vizualizacije
    private static final Color WALL_COLOR = Color.BLACK;
    private static final Color PATH_COLOR = new Color(255, 235, 59);
    private static final Color VISITED_COLOR = new Color(179, 229, 252);
    private static final Color START_COLOR = new Color(76, 175, 80);
    private static final Color GOAL_COLOR = new Color(244, 67, 54);
    private static final Color AGENT_COLOR = new Color(33, 150, 243);
    private static final Color EMPTY_COLOR = Color.WHITE;

    private static final int WALL_THICKNESS = 2;

    private Maze maze;
    private Cell agentPosition;
    private List<Cell> path;
    private Set<Cell> visitedCells;
    private final int cellSize;


    //Konstruktor panela.
    public MazePanel(Maze maze, int cellSize) {

        this.maze = Objects.requireNonNull(maze);
        this.cellSize = cellSize;
        this.path = new ArrayList<>();
        this.visitedCells = new HashSet<>();

        setBackground(Color.WHITE);

        int width = maze.getCols() * cellSize;
        int height = maze.getRows() * cellSize;

        setPreferredSize(new Dimension(width, height));

        System.out.println("MazePanel inicijaliziran. Dimenzije=" + width + "x" + height);
    }

    //Getteri i setteri

    public void setMaze(Maze maze) {
        this.maze = maze;
        repaint();
    }

    public void setAgentPosition(Cell position) {
        this.agentPosition = position;
        repaint();
    }

    public void setPath(List<Cell> path) {
        this.path = path != null ? new ArrayList<>(path) : new ArrayList<>();
        repaint();
    }

    public void setVisitedCells(Set<Cell> visited) {

        this.visitedCells = visited != null ? new HashSet<>(visited) : new HashSet<>();
        repaint();
    }

    public void clearVisited() {
        visitedCells.clear();
        repaint();
    }

    // Glavna metoda za crtanje Swing komponente
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (maze == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for(int row=0; row<maze.getRows(); row++) {
                for(int col=0; col< maze.getCols(); col++) {

                    Cell cell = maze.getCell(row, col);

                    int x=col * cellSize;
                    int y=row * cellSize;

                    drawCellBackground(g2, cell, x, y);
                    drawWalls(g2, cell, x, y);
                }
            }

            if (agentPosition != null) {

                int agentX = agentPosition.getCol() * cellSize;
                int agentY = agentPosition.getRow() * cellSize;

                drawAgent(g2, agentX, agentY);
            }

        } finally {
            g2.dispose();
        }
    }

    private void drawCellBackground(Graphics2D g2, Cell cell, int x, int y) {
        Color color = EMPTY_COLOR;

        if (visitedCells.contains(cell)) {
            color = VISITED_COLOR;
        }
        if (path.contains(cell)) {
            color = PATH_COLOR;
        }
        if (cell.equals(maze.getStart())) {
            color = START_COLOR;
        }
        if (cell.equals(maze.getGoal())) {
            color = GOAL_COLOR;
        }
        g2.setColor(color);

        g2.fillRect(x, y, cellSize, cellSize);
    }

    private void drawWalls(Graphics2D g2, Cell cell, int x, int y) {
        g2.setColor(WALL_COLOR);

        g2.setStroke(new BasicStroke(WALL_THICKNESS));

        if (cell.hasWall(Direction.UP)) {
            g2.drawLine(x, y, x + cellSize, y);
        }
        if (cell.hasWall(Direction.RIGHT)) {
            g2.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
        }
        if (cell.hasWall(Direction.DOWN)) {
            g2.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
        }
        if (cell.hasWall(Direction.LEFT)) {
            g2.drawLine(x, y, x, y + cellSize);
        }
    }

    private void drawAgent(Graphics2D g2, int x, int y) {
        int padding = cellSize / 4;
        g2.setColor(AGENT_COLOR);
        g2.fillOval(x+padding, y+padding, cellSize - 2*padding, cellSize - 2*padding);
    }
}
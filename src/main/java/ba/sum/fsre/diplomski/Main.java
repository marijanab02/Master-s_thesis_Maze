package ba.sum.fsre.diplomski;

import ba.sum.fsre.diplomski.maze.agent.*;
import ba.sum.fsre.diplomski.maze.algorithm.*;
import ba.sum.fsre.diplomski.maze.generator.DFSMazeGenerator;
import ba.sum.fsre.diplomski.maze.generator.ImperfectMazeGenerator;
import ba.sum.fsre.diplomski.maze.generator.MazeGenerator;
import ba.sum.fsre.diplomski.maze.model.Maze;
import ba.sum.fsre.diplomski.maze.qlearning.QLearningConfig;
import ba.sum.fsre.diplomski.maze.visualization.AnimationController;
import ba.sum.fsre.diplomski.maze.visualization.MazePanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int CELL_SIZE = 30;
    private static final int ANIM_DELAY_MS = 50;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Generiraj labirint
            //MazeGenerator generator = new DFSMazeGenerator();
            MazeGenerator generator = new ImperfectMazeGenerator();
            Maze maze = generator.generate(10, 10);

            //Pokreni sve agente
            DFSAgent dfsAgent = new DFSAgent();
            BFSAgent bfsAgent = new BFSAgent();
            AStarAgent aStarAgentEuk = new AStarAgent(new EuclideanHeuristic());
            AStarAgent aStarAgentMan = new AStarAgent(new ManhattanHeuristic());

            SearchResult dfsResult = dfsAgent.solve(maze);
            SearchResult bfsResult = bfsAgent.solve(maze);
            SearchResult aStarEukResult = aStarAgentEuk.solve(maze);
            SearchResult aStarManResult = aStarAgentMan.solve(maze);

            // Q-Learning: treniraj pa solve
            QLearningAgent qAgent = new QLearningAgent(QLearningConfig.defaultConfig());
            System.out.println("Počinjem Q-Learning trening");
            long t0 = System.currentTimeMillis();
            qAgent.train(maze);
            System.out.printf("Trening završen za %d ms%n", System.currentTimeMillis() - t0);
            SearchResult qResult = qAgent.solve(maze);

            // Definiraj sve panele koje treba prikazati
            record AgentDisplay(String label, MazePanel panel, List<ba.sum.fsre.diplomski.maze.model.Cell> explorationOrder, SearchResult result) {}

            MazePanel dfsMaze = new MazePanel(maze, CELL_SIZE);
            MazePanel bfsMaze = new MazePanel(maze, CELL_SIZE);
            MazePanel aStarMazeEuk = new MazePanel(maze, CELL_SIZE);
            MazePanel aStarMazeMan = new MazePanel(maze, CELL_SIZE);
            MazePanel qMaze = new MazePanel(maze, CELL_SIZE);

            List<AgentDisplay> displays = List.of(
                    new AgentDisplay("DFS", dfsMaze, dfsAgent.getExplorationOrder(), dfsResult),
                    new AgentDisplay("BFS", bfsMaze, bfsAgent.getExplorationOrder(), bfsResult),
                    new AgentDisplay("A* (Eukl.)", aStarMazeEuk, aStarAgentEuk.getExplorationOrder(), aStarEukResult),
                    new AgentDisplay("A* (Manhattan)", aStarMazeMan, aStarAgentMan.getExplorationOrder(), aStarManResult),
                    new AgentDisplay("Q-Learning", qMaze,  qResult.getPath(), qResult)
            );

            int cols = 3;
            int rows = (int) Math.ceil((double) displays.size() / cols);

            JPanel container = new JPanel(new GridLayout(rows, cols, 12, 12));
            container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            List<Runnable> animStarters = new ArrayList<>();

            for (AgentDisplay d : displays) {
                container.add(createLabeledPanel(d.label(), d.panel()));

                animStarters.add(() -> {
                    AnimationController ctrl = new AnimationController(d.panel());
                    ctrl.startAnimation(d.explorationOrder(), d.result(), ANIM_DELAY_MS);
                });
            }

            // ── 5. Prikaži jedan JFrame ────────────────────────────────────
            JFrame frame = new JFrame("Usporedba algoritama navigacije labirintom");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(container);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Pričekaj renderiranje pa pokreni animacije
            Timer startDelay = new Timer(300, e -> animStarters.forEach(Runnable::run));
            startDelay.setRepeats(false);
            startDelay.start();
        });
    }

    private static JPanel createLabeledPanel(String title, MazePanel mazePanel) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(label, BorderLayout.NORTH);
        panel.add(mazePanel, BorderLayout.CENTER);
        return panel;
    }
}
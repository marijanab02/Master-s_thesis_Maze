package ba.sum.fsre.diplomski;

import ba.sum.fsre.diplomski.maze.agent.QLearningAgent;
import ba.sum.fsre.diplomski.maze.algorithm.SearchResult;
import ba.sum.fsre.diplomski.maze.generator.DFSMazeGenerator;
import ba.sum.fsre.diplomski.maze.generator.MazeGenerator;
import ba.sum.fsre.diplomski.maze.model.Maze;
import ba.sum.fsre.diplomski.maze.qlearning.EpisodeReplay;
import ba.sum.fsre.diplomski.maze.qlearning.QLearningConfig;
import ba.sum.fsre.diplomski.maze.visualization.AnimationController;
import ba.sum.fsre.diplomski.maze.visualization.MazePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

//Vizualizacija treninga QLearning agenta
public class QLearningTrainingVisualizer {

    private static final int CELL_SIZE= 30;
    private static final int ANIM_DELAY_MS = 50;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            MazeGenerator generator = new DFSMazeGenerator();
            Maze maze = generator.generate(10, 10);

            QLearningAgent agent = new QLearningAgent(QLearningConfig.defaultConfig());

            System.out.println("Počinjem trening");
            long t0 = System.currentTimeMillis();
            agent.train(maze);
            System.out.printf("Trening završen za %d ms%n", (System.currentTimeMillis() - t0));

            List<EpisodeReplay> all = agent.getSavedEpisodes();

            showGrid(maze, all);
        });
    }

    private static void showGrid(Maze maze, List<EpisodeReplay> episodes) {

        int count = episodes.size();
        int cols = Math.min(count, 3);
        int rows = (int)Math.ceil((double)count/cols);

        JPanel container = new JPanel(new GridLayout(rows, cols, 12, 12));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Runnable> animationStarters = new java.util.ArrayList<>();

        for(EpisodeReplay replay : episodes) {

            MazePanel panel = new MazePanel(maze, CELL_SIZE);
            String label = "Epizoda " + replay.episode();
            container.add(createLabeledPanel(label, panel));

            //Pripremi animaciju-pokrenut će se nakon što prozor postane vidljiv
            animationStarters.add(() -> {
                AnimationController ctrl = new AnimationController(panel);
                ctrl.startAnimation(
                        replay.visitedCells(),
                        SearchResult.success(
                                replay.visitedCells(),
                                "Epizoda " + replay.episode(),
                                replay.visitedCells().size(),
                                0,
                                0
                        ),
                        ANIM_DELAY_MS
                );
            });
        }

        JFrame frame = new JFrame("Q-Learning Training Progress");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(container);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer startDelay = new Timer(300, e -> {animationStarters.forEach(Runnable::run);});
        startDelay.setRepeats(false);
        startDelay.start();
    }

    private static JPanel createLabeledPanel(String title, MazePanel mazePanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(mazePanel, BorderLayout.CENTER);
        return panel;
    }
}
package ba.sum.fsre.diplomski.maze.qlearning;

import ba.sum.fsre.diplomski.maze.model.Cell;

import java.util.List;

public record EpisodeReplay(
        int episode,
        List<Cell> visitedCells,
        double reward,
        int steps
) {
}
# Maze Navigation with Search Algorithms and Reinforcement Learning

A Java implementation and experimental comparison of classical search algorithms (DFS, BFS, A*) and Q-learning agent on the maze navigation problem. Built as part of a diploma thesis (*"Design and Analysis of Search and Reinforcement Learning Algorithms in the Maze Navigation Problem"*).

The project generates mazes (both perfect and imperfect/cyclic), runs each agent against them, visualizes the search process with a Swing-based animation, and produces CSV datasets used to benchmark scalability, path optimality, and Q-learning convergence.

## Features

- **Maze generation**
    - Perfect mazes via randomized DFS (recursive backtracking)
    - Imperfect mazes (with cycles) by randomly removing extra walls from a perfect maze
    - Seed-based generation for fully reproducible experiments
- **Navigation agents**
    - Depth-First Search (DFS)
    - Breadth-First Search (BFS)
    - A* with Manhattan heuristic
    - A* with Euclidean heuristic
    - Q-learning (tabular, ε-greedy exploration)
- **Visualization**
    - Swing-based maze rendering with live animation of the agent's exploration and final path
    - Side-by-side comparison view of all agents solving the same maze
    - Q-learning training-progress visualizer (snapshot of the agent's behavior at different training episodes)
- **Experiments**
    - Scalability across maze sizes (10×10 up to 100×100)
    - Path optimality ratio relative to BFS baseline
    - Q-learning convergence across hyperparameter combinations (α, γ, ε-decay)
    - Raw and aggregated CSV exports for further analysis

## Project structure

```
src/main/java/ba/sum/fsre/diplomski/
├── maze/
│   ├── model/          # Cell, Maze, Direction — core maze representation
│   ├── generator/       # MazeGenerator, DFSMazeGenerator, ImperfectMazeGenerator
│   ├── agent/           # Agent interface + DFS, BFS, A*, Q-learning implementations
│   ├── algorithm/       # SearchResult, Heuristic interface + implementations
│   ├── qlearning/       # QTable, RewardFunction, EpsilonGreedyPolicy, QLearningConfig
│   ├── visualization/   # MazePanel, AnimationController
│   └── experiment/      # Experiment runners, config, CSV export, result aggregation
├── Main.java            # Entry point: side-by-side agent comparison
└── QLearningVisulizer   # Training-progress visualizer
results/                 # CSV output from experiment runs (generated, not committed)
python/charts                  # Python-generated charts from experiment CSVs (generated, not committed)
```

## Requirements

- Java 17+
- Maven (or your IDE's built-in build tool)
- Python 3.9+ with `pandas`, `matplotlib`, `numpy` (only needed for chart generation, not for running the Java code)

## Building

```bash
mvn clean install
```

Or simply open the project in IntelliJ IDEA / Eclipse and let it resolve dependencies via the included `pom.xml`.

## Running

### Visual comparison of all agents on one maze

Runs DFS, BFS, A* (Euclidean), and Q-learning on the same generated maze and displays all four side by side in a single window, animated.

```bash
mvn compile exec:java -Dexec.mainClass="ba.sum.fsre.diplomski.Main"
```

### Q-learning training progress visualizer

Trains a Q-learning agent and displays a grid of snapshots showing how its behavior evolves across training episodes (e.g. episode 0, 50, 100, 500, last episode).

```bash
mvn compile exec:java -Dexec.mainClass="ba.sum.fsre.diplomski.QLearningTrainingVisualizer"
```

### Running experiments

Each experiment is a standalone `main()` class. Results are written as CSV files into `results/`.

**Experiment 1 — Scalability** (DFS/BFS/A*/Q-learning across maze sizes 10×10–100×100):

```bash
mvn compile exec:java -Dexec.mainClass="ba.sum.fsre.diplomski.maze.experiment.Experiment1Launcher"
```

**Experiment 2 — Q-learning convergence** (α × γ × ε-decay grid search):

```bash
mvn compile exec:java -Dexec.mainClass="ba.sum.fsre.diplomski.maze.experiment.Experiment2Launcher"
```

> Note: experiment configuration (maze sizes, number of runs, hyperparameter ranges, etc.) is defined in the corresponding `*Config.java` class for each experiment - edit those constants directly to change experiment scope before running.

### Generating charts from results

Each experiment's CSV output can be turned into charts using the accompanying Python scripts (run from the project root, where `results/` lives):

```bash
pip install pandas matplotlib numpy
python generate_experiment1_charts.py
python generate_experiment2_charts.py
python generate_experiment3_charts.py
```

Charts are written to `charts/`.

## Algorithms at a glance

| Agent | Guarantees optimal path? | Notes |
|---|---|---|
| DFS | No | Lowest memory footprint, but can return significantly longer paths in mazes with cycles |
| BFS | Yes | Used as the optimality baseline in experiments |
| A* (Manhattan) | Yes (admissible heuristic) | Generally efficient on grid-based movement |
| A* (Euclidean) | Yes (admissible heuristic) | Less informative than Manhattan for 4-directional movement; can underperform BFS on cyclic mazes |
| Q-learning | No formal guarantee | Learns a near-optimal policy given enough training episodes; doesn't generalize across different maze instances (tabular representation) |

## License

This project was developed as part of an academic thesis. Feel free to use it for learning purposes.
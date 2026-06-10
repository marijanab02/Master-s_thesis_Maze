"""
Generira grafove za Eksperiment 1: Skalabilnost
"""

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

CSV_PATH   = "experiment1_aggregated.csv"
OUTPUT_DIR = "charts"
os.makedirs(OUTPUT_DIR, exist_ok=True)

STYLE = {
    "Depth-First Search": dict(color="#E53935", marker="o",  linestyle="-",  linewidth=2.0, markersize=6),
    "Breadth-First Search": dict(color="#1E88E5", marker="s",  linestyle="-",  linewidth=2.0, markersize=6),
    "A* (Manhattan)": dict(color="#43A047", marker="^",  linestyle="-",  linewidth=2.0, markersize=6),
    "A* (Euclidean)": dict(color="#FB8C00", marker="D",  linestyle="--", linewidth=1.8, markersize=5),
    "Q-Learning": dict(color="#8E24AA", marker="*",  linestyle=":",  linewidth=2.0, markersize=9),
}
ALGO_ORDER = list(STYLE.keys())

df = pd.read_csv(CSV_PATH)

# NaN za Q-Learning gdje nema uspjeha
df.loc[(df["algorithmName"] == "Q-Learning") & (df["successRate"] == 0),
       ["avgPathLength","avgNodesExplored","avgExecutionTimeMs",
        "avgMemoryBytes","stdPathLength","stdNodesExplored","stdExecutionTimeMs"]] = np.nan

sizes = sorted(df["mazeSize"].unique())

def plot_metric(ax, metric_col, std_col, ylabel, title, log_scale=False, memory_mb=False):
    for algo in ALGO_ORDER:
        sub = df[df["algorithmName"] == algo].sort_values("mazeSize")
        if sub.empty:
            continue
        x = sub["mazeSize"].values
        y = sub[metric_col].values.copy().astype(float)
        std = sub[std_col].values.copy().astype(float) if std_col else np.zeros_like(y)
        if memory_mb:
            y = y/(1024*1024)
            std = std / (1024*1024)
        style = STYLE[algo]
        ax.plot(x, y, label=algo, **style)
        mask = ~np.isnan(y)
        if std_col and mask.any():
            ax.fill_between(x[mask], (y-std)[mask], (y+std)[mask],
                            alpha=0.12, color=style["color"])
    if log_scale: 
        ax.set_yscale("log")
    ax.set_xlabel("Dimenzija labirinta (N×N)", fontsize=11)
    ax.set_ylabel(ylabel, fontsize=11)
    ax.set_title(title, fontsize=13, fontweight="bold", pad=10)
    ax.set_xticks(sizes)
    ax.set_xticklabels([f"{s}×{s}" for s in sizes])
    ax.legend(fontsize=9, loc="upper left", framealpha=0.85)
    ax.grid(True, linestyle="--", alpha=0.45)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)

# Individualni grafovi

metrics = [
    ("avgPathLength", "stdPathLength", "Prosječna duljina puta (broj koraka)", "Eksperiment 1 - Duljina pronađenog puta",  False, False, "exp1_path_length.png"),
    ("avgNodesExplored", "stdNodesExplored",  "Prosječan broj istraženih čvorova", "Eksperiment 1 - Broj istraženih čvorova", True,  False, "exp1_nodes_explored.png"),
    ("avgExecutionTimeMs","stdExecutionTimeMs","Prosječno vrijeme izvođenja (ms)", "Eksperiment 1 - Vrijeme izvođenja", False, False, "exp1_execution_time.png"),
    ("avgMemoryBytes", None, "Prosječna memorijska potrošnja (MB)", "Eksperiment 1 - Memorijska potrošnja", False, True, "exp1_memory.png"),
]

for mcol, scol, ylabel, title, logsc, membool, fname in metrics:
    fig, ax = plt.subplots(figsize=(9, 5.5))
    plot_metric(ax, mcol, scol, ylabel, title, log_scale=logsc, memory_mb=membool)
    fig.tight_layout()
    out = f"{OUTPUT_DIR}/{fname}"
    fig.savefig(out, dpi=150, bbox_inches="tight")
    plt.close(fig)
    print(f"Generirano: {out}")

# Kombinirani pregled (2x2)

fig, axes = plt.subplots(2, 2, figsize=(16, 10))
fig.suptitle("Eksperiment 1: Skalabilnost algoritama navigacije labirintom",
             fontsize=15, fontweight="bold", y=1.01)

plot_metric(axes[0,0], "avgPathLength", "stdPathLength",
            "Duljina puta (koraci)", "Duljina pronađenog puta")
plot_metric(axes[0,1], "avgNodesExplored",  "stdNodesExplored",
            "Istraženi čvorovi", "Broj istraženih čvorova", log_scale=True)
plot_metric(axes[1,0], "avgExecutionTimeMs","stdExecutionTimeMs",
            "Vrijeme izvođenja (ms)","Vrijeme izvođenja")
plot_metric(axes[1,1], "avgMemoryBytes", None,
            "Memorija (MB)", "Memorijska potrošnja", memory_mb=True)

handles, labels = axes[0,0].get_legend_handles_labels()
for ax in axes.flat:
    leg = ax.get_legend()
    if leg: leg.remove()

fig.legend(handles, labels, loc="lower center", ncol=len(ALGO_ORDER),
           fontsize=10, framealpha=0.9, bbox_to_anchor=(0.5, -0.04))
fig.tight_layout()
out = f"{OUTPUT_DIR}/exp1_overview.png"
fig.savefig(out, dpi=150, bbox_inches="tight")
plt.close(fig)
print(f"Generirano: {out}")
print(f"\nSvi grafovi u: {OUTPUT_DIR}/")

"""
Generira grafove za Eksperiment 2: Q-learning konvergencija"""

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
import numpy as np
import os

#Konfiguracija

CSV_PATH   = "experiment2_1000ep_30ms_raw.csv"
OUTPUT_DIR = "charts_Q_learning_200ep_30ms"
os.makedirs(OUTPUT_DIR, exist_ok=True)


#Učitaj i pripremi podatke

df = pd.read_csv(CSV_PATH)
print(f"Učitano {len(df):,} redova.")
print(f"Kombinacije: {df[['alpha','gamma','epsilonStrategy']].drop_duplicates().shape[0]}")

# Agregiraj po (kombinacija, epizoda) - prosjek preko svih runova
def aggregate(df):
    return df.groupby(
        ["alpha", "gamma", "epsilonStrategy", "episode"]
    ).agg(
        avgReward = ("totalReward", "mean"),
        stdReward = ("totalReward", "std"),
        avgRolling = ("rollingAvgReward","mean"),
        avgSteps = ("steps", "mean"),
        successRate = ("reachedGoal", "mean"),  # udio runova koji su dostigli cilj
    ).reset_index()

agg = aggregate(df)

# Konstante iz podataka
ALPHAS = sorted(df["alpha"].unique())
GAMMAS = sorted(df["gamma"].unique())
STRATEGIES = sorted(df["epsilonStrategy"].unique())

# Boje
ALPHA_COLORS = {a: c for a, c in zip(ALPHAS, ["#E53935","#1E88E5","#43A047","#FB8C00"])}
GAMMA_COLORS = {g: c for g, c in zip(GAMMAS, ["#5E35B1","#00ACC1","#F4511E"])}
STRATEGY_COLORS= {s: c for s, c in zip(STRATEGIES, ["#6D4C41","#00897B","#3949AB"])}

def plot_learning_curve(ax, data, label, color, show_std=True):
    episodes = data["episode"].values
    avg = data["avgRolling"].values
    std = data["stdReward"].fillna(0).values

    ax.plot(episodes, avg, label=label, color=color, linewidth=1.8)
    if show_std:
        ax.fill_between(episodes,
                        avg - 0.5*std,
                        avg + 0.5*std,
                        alpha=0.15, color=color)

def style_ax(ax, title, xlabel="Epizoda", ylabel="Prosječna nagrada (rolling avg)"):
    ax.set_title(title, fontweight="bold", pad=8)
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)
    ax.axhline(0, color="gray", linewidth=0.7, linestyle="--", alpha=0.5)
    ax.legend(loc="lower right", framealpha=0.85)
    ax.grid(True, linestyle="--", alpha=0.4)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)

# Graf 1: Learning curve po alpha

fixed_gamma = 0.9
fixed_strategy = STRATEGIES[1]   # Srednji decay

fig, ax = plt.subplots(figsize=(10, 5.5))

for alpha in ALPHAS:
    data = agg[
        (agg["alpha"] == alpha) &
        (agg["gamma"]== fixed_gamma) &
        (agg["epsilonStrategy"] == fixed_strategy)
    ]
    if not data.empty:
        plot_learning_curve(ax, data,
                            label=f"α = {alpha}",
                            color=ALPHA_COLORS[alpha])

style_ax(ax,
    f"Utjecaj learning rate (α) na konvergenciju\n"
    f"γ={fixed_gamma}, ε-decay={fixed_strategy}")

fig.tight_layout()
out = f"{OUTPUT_DIR}/exp2_alpha_comparison.png"
fig.savefig(out, bbox_inches="tight")
plt.close(fig)
print(f"{out}")

# Graf 2: Learning curve po gamma

fixed_alpha = 0.3

fig, ax = plt.subplots(figsize=(10, 5.5))

for gamma in GAMMAS:
    data = agg[
        (agg["alpha"] == fixed_alpha) &
        (agg["gamma"] == gamma) &
        (agg["epsilonStrategy"] == fixed_strategy)
    ]
    if not data.empty:
        plot_learning_curve(ax, data,
                            label=f"γ = {gamma}",
                            color=GAMMA_COLORS[gamma])

style_ax(ax,
    f"Utjecaj discount faktora (γ) na konvergenciju\n"
    f"α={fixed_alpha}, ε-decay={fixed_strategy}")

fig.tight_layout()
out = f"{OUTPUT_DIR}/exp2_gamma_comparison.png"
fig.savefig(out, bbox_inches="tight")
plt.close(fig)
print(f"{out}")

# Graf 3: Learning curve po epsilon-decay strategiji
fig, ax = plt.subplots(figsize=(10, 5.5))

for strategy, color in STRATEGY_COLORS.items():
    data = agg[
        (agg["alpha"] == fixed_alpha) &
        (agg["gamma"] == fixed_gamma) &
        (agg["epsilonStrategy"] == strategy)
    ]
    if not data.empty:
        plot_learning_curve(ax, data,
                            label=strategy,
                            color=color)

style_ax(ax,
    f"Utjecaj ε-decay strategije na konvergenciju\n"
    f"α={fixed_alpha}, γ={fixed_gamma}")

fig.tight_layout()
out = f"{OUTPUT_DIR}/exp2_epsilon_comparison.png"
fig.savefig(out, bbox_inches="tight")
plt.close(fig)
print(f"{out}")

# Graf 4: Heatmap - finalna prosječna nagrada po alpha×gamma
FINAL_WINDOW = 100
final_episodes = agg[agg["episode"] >= (agg["episode"].max() - FINAL_WINDOW + 1)]

pivot_data = {}

for strategy in STRATEGIES:
    sub = final_episodes[final_episodes["epsilonStrategy"] == strategy]
    pivot = sub.groupby(["alpha","gamma"])["avgReward"].mean().unstack("gamma")
    pivot_data[strategy] = pivot

fig, axes = plt.subplots(1, len(STRATEGIES), figsize=(16, 5), sharey=True)
fig.suptitle(
    "Heatmap: Finalna prosječna nagrada po α×γ kombinaciji\n"
    "(prosječne zadnjih 100 epizoda)",
    fontsize=13, fontweight="bold"
)

for ax, strategy in zip(axes, STRATEGIES):
    pivot = pivot_data.get(strategy)
    if pivot is None or pivot.empty:
        continue

    im = ax.imshow(pivot.values, cmap="RdYlGn", aspect="auto",
                   vmin=-50, vmax=80)
    ax.set_xticks(range(len(GAMMAS)))
    ax.set_xticklabels([f"γ={g}" for g in GAMMAS], rotation=20)
    ax.set_yticks(range(len(ALPHAS)))
    ax.set_yticklabels([f"α={a}" for a in ALPHAS])
    ax.set_title(strategy, fontsize=10)

    # Vrijednosti u ćelijama
    for i in range(pivot.shape[0]):
        for j in range(pivot.shape[1]):
            val = pivot.values[i, j]
            if not np.isnan(val):
                ax.text(j, i, f"{val:.0f}",
                        ha="center", va="center",
                        fontsize=9,
                        color="black" if -20 < val < 60 else "white")

fig.colorbar(im, ax=axes[-1], label="Prosječna nagrada")
fig.tight_layout()
out = f"{OUTPUT_DIR}/exp2_heatmap.png"
fig.savefig(out, bbox_inches="tight")
plt.close(fig)
print(f"{out}")

# Graf 5: Stopa uspjeha (% epizoda u kojima je agent dostigao cilj)
# Smoothing za success rate
SMOOTH = 100
# 12 jedinstvenih boja za sve α×γ kombinacije
import itertools
COMBO_COLORS = {
    (a, g): c
    for (a, g), c in zip(
        [(a, g) for a in ALPHAS for g in GAMMAS],
        [
            "#E53935", "#D81B60", "#8E24AA",
            "#1E88E5", "#00ACC1", "#00897B",
            "#43A047", "#C0CA33", "#FB8C00",
            "#6D4C41", "#546E7A", "#3949AB",
        ]
    )
}
fig, axes = plt.subplots(1, 3, figsize=(17, 5), sharey=True)
fig.suptitle(
    "Stopa uspjeha (% epizoda s dostignutim ciljem) - po α×γ kombinacijama",
    fontsize=13, fontweight="bold"
)

for ax, strategy in zip(axes, STRATEGIES):
    for alpha in ALPHAS:
        for gamma in GAMMAS:
            data = agg[
                (agg["alpha"] == alpha) &
                (agg["gamma"] == gamma) &
                (agg["epsilonStrategy"] == strategy)
            ].sort_values("episode")

            if data.empty:
                continue

            # Rolling average stope uspjeha
            sr = data["successRate"].rolling(SMOOTH, min_periods=1).mean()
            ax.plot(data["episode"], sr,
                    linewidth=1.2, alpha=0.75,
                    color=COMBO_COLORS[(alpha, gamma)],
                    label=f"α={alpha},γ={gamma}")

    ax.set_title(strategy, fontsize=10)
    ax.set_xlabel("Epizoda")
    ax.set_ylim(-0.05, 1.05)
    ax.set_yticks([0, 0.25, 0.5, 0.75, 1.0])
    ax.set_yticklabels(["0%", "25%", "50%", "75%", "100%"])
    ax.axhline(1.0, color="green", linewidth=0.8, linestyle="--", alpha=0.5)
    ax.grid(True, linestyle="--", alpha=0.35)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)

axes[0].set_ylabel("Stopa uspjeha (rolling avg)")
axes[1].legend(loc="upper left", fontsize=7,
               ncol=2, framealpha=0.8,
               bbox_to_anchor=(0.0, 1.0))

fig.tight_layout()
out = f"{OUTPUT_DIR}/exp2_success_rate.png"
fig.savefig(out, bbox_inches="tight")
plt.close(fig)
print(f"✓ {out}")

# Graf 6: Kombinirani pregled - grid 4×3 (alpha × gamma)

fig, axes = plt.subplots(len(ALPHAS), len(GAMMAS),
                          figsize=(16, 14),
                          sharex=True, sharey=True)

fig.suptitle(
    "Pregled konvergencije za sve α×γ kombinacije\n"
    "(linije = ε-decay strategija, os x = epizoda, os y = rolling avg nagrada)",
    fontsize=13, fontweight="bold", y=1.01
)

for i, alpha in enumerate(ALPHAS):
    for j, gamma in enumerate(GAMMAS):
        ax = axes[i][j]

        for strategy, color in STRATEGY_COLORS.items():
            data = agg[
                (agg["alpha"] == alpha) &
                (agg["gamma"] == gamma) &
                (agg["epsilonStrategy"] == strategy)
            ].sort_values("episode")

            if not data.empty:
                ax.plot(data["episode"],
                        data["avgRolling"],
                        color=color,
                        linewidth=1.4,
                        label=strategy)

        ax.set_title(f"α={alpha}, γ={gamma}", fontsize=9, pad=4)
        ax.axhline(0, color="gray", linewidth=0.6, linestyle="--", alpha=0.5)
        ax.grid(True, linestyle="--", alpha=0.3)
        ax.spines["top"].set_visible(False)
        ax.spines["right"].set_visible(False)

        if i == len(ALPHAS) - 1:
            ax.set_xlabel("Epizoda", fontsize=8)
        if j == 0:
            ax.set_ylabel("Avg nagrada", fontsize=8)

# Zajednička legenda
handles = [plt.Line2D([0],[0], color=c, linewidth=2, label=s)
           for s, c in STRATEGY_COLORS.items()]
fig.legend(handles=handles,
           loc="lower center", ncol=len(STRATEGIES),
           fontsize=10, framealpha=0.9,
           bbox_to_anchor=(0.5, -0.02))

fig.tight_layout()
out = f"{OUTPUT_DIR}/exp2_full_grid.png"
fig.savefig(out, bbox_inches="tight")
plt.close(fig)
print(f"✓ {out}")

print(f"\nSvi grafovi u: {OUTPUT_DIR}/")
print("Generirani fajlovi:")
for f in sorted(os.listdir(OUTPUT_DIR)):
    if f.startswith("exp2"):
        print(f"  {f}")

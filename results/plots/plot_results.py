"""
Generates the required plots (Execution Time vs n, Operations vs n)
for each of the four workloads from the CSVs in results/tables/.

Run from the project root:
    python3 results/plots/plot_results.py

Requires: pandas, matplotlib (pip install pandas matplotlib --break-system-packages)
Output: PNG files written into results/plots/
"""
import os
import pandas as pd
import matplotlib.pyplot as plt

HERE = os.path.dirname(os.path.abspath(__file__))
TABLES = os.path.join(HERE, "..", "tables")
OUT = HERE


def save(fig, name):
    path = os.path.join(OUT, name)
    fig.savefig(path, dpi=150, bbox_inches="tight")
    print("wrote", path)
    plt.close(fig)


def plot_workload1():
    df = pd.read_csv(os.path.join(TABLES, "workload1_random_access.csv"))

    fig, ax = plt.subplots(figsize=(7, 5))
    for structure, g in df.groupby("structure"):
        ax.plot(g["n"], g["avg_time_ns"] / 1e6, marker="o", label=structure)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (initial elements)")
    ax.set_ylabel("avg execution time for 10,000 get() calls (ms)")
    ax.set_title("Workload 1 - Random Access: Execution Time vs n")
    ax.legend()
    ax.grid(True, which="both", alpha=0.3)
    save(fig, "w1_time_vs_n.png")

    fig, ax = plt.subplots(figsize=(7, 5))
    for structure, g in df.groupby("structure"):
        ax.plot(g["n"], g["accesses"], marker="o", label=structure)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (initial elements)")
    ax.set_ylabel("total element accesses (10,000 get() calls)")
    ax.set_title("Workload 1 - Random Access: Accesses vs n")
    ax.legend()
    ax.grid(True, which="both", alpha=0.3)
    save(fig, "w1_accesses_vs_n.png")


def plot_workload2():
    df = pd.read_csv(os.path.join(TABLES, "workload2_search.csv"))

    fig, ax = plt.subplots(figsize=(7, 5))
    for structure, g in df.groupby("structure"):
        ax.plot(g["n"], g["avg_time_ns"] / 1e6, marker="o", label=structure)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (initial elements)")
    ax.set_ylabel("avg execution time for 1,000 contains() calls (ms)")
    ax.set_title("Workload 2 - Search: Execution Time vs n")
    ax.legend()
    ax.grid(True, which="both", alpha=0.3)
    save(fig, "w2_time_vs_n.png")

    fig, ax = plt.subplots(figsize=(7, 5))
    for structure, g in df.groupby("structure"):
        ax.plot(g["n"], g["comparisons"], marker="o", label=structure)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (initial elements)")
    ax.set_ylabel("total comparisons (1,000 contains() calls)")
    ax.set_title("Workload 2 - Search: Comparisons vs n")
    ax.legend()
    ax.grid(True, which="both", alpha=0.3)
    save(fig, "w2_comparisons_vs_n.png")


def plot_workload3():
    df = pd.read_csv(os.path.join(TABLES, "workload3_insert_remove.csv"))

    for position in ["front", "middle"]:
        sub = df[df["position"] == position]
        fig, ax = plt.subplots(figsize=(7.5, 5))
        for (structure, op), g in sub.groupby(["structure", "operation"]):
            ax.plot(g["n"], g["avg_time_ns"] / 1e6, marker="o", label=f"{structure} {op}")
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.set_xlabel("n (initial elements)")
        ax.set_ylabel("avg execution time for 1,000 ops (ms)")
        ax.set_title(f"Workload 3 - Insert/Remove at {position}: Time vs n")
        ax.legend(fontsize=8)
        ax.grid(True, which="both", alpha=0.3)
        save(fig, f"w3_time_vs_n_{position}.png")

    fig, ax = plt.subplots(figsize=(7.5, 5))
    for (structure, op), g in df.groupby(["structure", "operation"]):
        if op != "insert":
            continue
        style = "-" if "front" in g["position"].values else "--"
        for position, gg in g.groupby("position"):
            ls = "-" if position == "front" else "--"
            ax.plot(gg["n"], gg["movements"], marker="o", linestyle=ls,
                     label=f"{structure} insert ({position})")
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (initial elements)")
    ax.set_ylabel("total element movements (1,000 insertions)")
    ax.set_title("Workload 3 - Insertion: Movements vs n")
    ax.legend(fontsize=7)
    ax.grid(True, which="both", alpha=0.3)
    save(fig, "w3_movements_vs_n.png")


def plot_workload4():
    df = pd.read_csv(os.path.join(TABLES, "workload4_priority_processing.csv"))

    fig, ax = plt.subplots(figsize=(7, 5))
    ax.plot(df["n"], df["avg_insert_time_ns"] / 1e6, marker="o", label="insert (n calls)")
    ax.plot(df["n"], df["avg_extract_time_ns"] / 1e6, marker="s", label="extractMin (n calls)")
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n")
    ax.set_ylabel("avg execution time (ms)")
    ax.set_title("Workload 4 - MinHeap: Execution Time vs n")
    ax.legend()
    ax.grid(True, which="both", alpha=0.3)
    save(fig, "w4_time_vs_n.png")

    fig, ax = plt.subplots(figsize=(7, 5))
    ax.plot(df["n"], df["insert_comparisons"], marker="o", label="insert comparisons")
    ax.plot(df["n"], df["extract_comparisons"], marker="s", label="extractMin comparisons")
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n")
    ax.set_ylabel("total comparisons")
    ax.set_title("Workload 4 - MinHeap: Comparisons vs n")
    ax.legend()
    ax.grid(True, which="both", alpha=0.3)
    save(fig, "w4_comparisons_vs_n.png")


if __name__ == "__main__":
    plot_workload1()
    plot_workload2()
    plot_workload3()
    plot_workload4()
    print("All plots generated.")

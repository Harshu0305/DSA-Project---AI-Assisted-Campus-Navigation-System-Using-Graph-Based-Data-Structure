# AI-Assisted Campus Navigation System Using Graph-Based Data Structures

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Data Structures](https://img.shields.io/badge/DSA-Graph%20%7C%20A*%20%7C%20Dijkstra-blue?style=for-the-badge)](https://en.wikipedia.org/wiki/A*_search_algorithm)
[![Institution](https://img.shields.io/badge/Campus-VIT--AP%20University-721422?style=for-the-badge)](https://vitap.ac.in/)
[![Build](https://img.shields.io/badge/Build-Pure%20Java%20(Zero%20Dependencies)-success?style=for-the-badge)](#)

A core Java pedestrian routing engine designed for the **VIT-AP University** campus. Built from first principles using standard Java collections, this project models physical campus topology as an attributed directed graph and utilizes **$A^*$ Heuristic Search** alongside an **AI-driven Dynamic Cost Engine** to optimize routes based on real-time weather conditions, pedestrian rush hours, mobility constraints, and campus security policies.

---

## 📌 Table of Contents
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Algorithmic Core & Complexity](#-algorithmic-core--complexity)
- [Campus Topology & Security Zones](#-campus-topology--security-zones)
- [Project Directory Structure](#-project-directory-structure)
- [Getting Started](#-getting-started)
- [Interactive Modes & Usage](#-interactive-modes--usage)
- [Benchmarking Telemetry](#-benchmarking-telemetry)
- [Academic Information](#-academic-information)

---

## 🚀 Key Features

* **$A^*$ Heuristic Search Engine:** Calculates mathematically optimal paths using an admissible straight-line Euclidean distance heuristic, pruning search space by **47%–58%** compared to standard Dijkstra search.
* **Dynamic Environmental Cost Engine:**
  * **🌧️ Monsoon Rain Mode:** Penalizes exposed open-air walkways ($2.5\times$ multiplier) and prioritizes covered skyways (such as the Central Block to Kalam Block corridors with a $0.85\times$ incentive).
  * **☀️ Extreme Heat Mode:** Applies heat resistance penalties ($2.0\times$) across unshaded open grounds (e.g., Sports Triangle).
  * **⏰ Class Change Rush Hour:** Dynamically scales pedestrian bottleneck costs ($1.5\times$) around primary academic complexes during scheduled slot transition breaks.
* **♿ Step-Free Universal Accessibility:** Dynamically assigns $+\infty$ cost to edges with stairs (such as the tiered Rock Plaza amphitheatre steps), seamlessly rerouting mobility-impaired users to paved ramps.
* **🛡️ Role-Based Security & Policy Enforcement:**
  * Enforces institutional gender-segregated residential boundaries: male student profiles are barred from Ladies' Hostel clusters, and female student profiles are barred from Men's Hostel clusters.
  * Bypasses hazardous, off-limits interior construction zones by strictly routing along paved outer corridors.
* **💬 Conversational Query Parser:** An integrated Natural Language Processing (NLP) token-matcher extracts origins, destinations, weather states, and user roles from natural English prompts (e.g., *"Male student from Food Street to Kalam Block without stairs in heavy rain"*).
* **⚡ Zero External Dependencies:** Implemented purely in Java standard library (`java.util.*`), requiring no external frameworks or build plugins.

---

## 🏛️ System Architecture

```text
+----------------------------------------------------------------------+
|                           USER INTERFACE                             |
|         (Interactive CLI Console  |  Natural Language Parser)         |
+----------------------------------+-----------------------------------+
                                   |
                                   v
+----------------------------------------------------------------------+
|                     CONTEXT & INTENT ENGINE                          |
|   CampusQueryParser  <--------->  NavigationContext                  |
|   - Entity Matching               - Weather: Rain / Clear / Sun      |
|   - Constraint Extraction         - Role: Male / Female / Faculty    |
+----------------------------------+-----------------------------------+
                                   |
                                   v
+----------------------------------------------------------------------+
|                       DYNAMIC COST ENGINE                            |
|   EdgeWeightProvider: Evaluates Effective Weight W(e)                |
|   - Rain Penalty (2.5x)           - Indoor Corridor Bonus (0.85x)    |
|   - Stair Filter (+Infinity)      - Security Zone Gate (+Infinity)   |
+----------------------------------+-----------------------------------+
                                   |
                                   v
+----------------------------------------------------------------------+
|                     ALGORITHMIC ROUTING CORE                         |
|   AStarRouter (f = g + h)        |  DijkstraRouter (Baseline)        |
|   - Min-PriorityQueue (Heap)     - Uniform Search Frontier           |
|   - Euclidean Distance Heuristic - Uninformed Radial Expansion       |
+----------------------------------+-----------------------------------+
                                   |
                                   v
+----------------------------------------------------------------------+
|                       CAMPUS GRAPH STORAGE                           |
|   CampusGraph: Adjacency List [Map<String, List<Edge>>]              |
|   - Vertices: Metric Coordinates (X, Y), Security Zone, Category     |
|   - Edges: Base Distance, Covered Flag, Stair Flag                   |
+----------------------------------------------------------------------+
```

---

## 🧮 Algorithmic Core & Complexity

### Evaluation Function
$$f(n) = g(n) + h(n)$$
* $g(n)$: Exact accumulated metric cost from the origin to node $n$.
* $h(n)$: Euclidean straight-line distance heuristic to destination:
  $$h(n) = \sqrt{(x_n - x_{\text{goal}})^2 + (y_n - y_{\text{goal}})^2}$$

### Heuristic Admissibility Proof
Because Euclidean distance represents the absolute shortest distance between two points in $\mathbb{R}^2$, and all physical ground walking segments $w(u, v) \ge \text{dist}(u, v)$ with dynamic multipliers $M \ge 1.0$, the condition:
$$0 \le h(n) \le h^*(n)$$
strictly holds for all nodes. Thus, $A^*$ is **mathematically guaranteed to return the optimal shortest path**.

### Complexity Breakdown

| Data Structure / Algorithm | Time Complexity | Space Complexity |
| :--- | :--- | :--- |
| **Adjacency List (`CampusGraph`)** | $O(1)$ add, $O(\text{deg}(V))$ lookup | $O(|V| + |E|)$ |
| **Dijkstra's Algorithm** | $O((|V| + |E|) \log |V|)$ | $O(|V| + |E|)$ |
| **$A^*$ Search Algorithm** | $O((|V| + |E|) \log |V|)$ | $O(|V| + |E|)$ |
| **Min-Heap (`PriorityQueue`)** | $O(\log |V|)$ insert/extract | $O(|V|)$ |
| **Visited Set / Score Maps** | $O(1)$ average | $O(|V|)$ |

---

## 🗺️ Campus Topology & Security Zones

The topological graph is mapped from Google satellite telemetry anchored at the **Main Security Gate $(0, 0)$**:

| Node ID | Landmark Name | Coordinates $(X, Y)$ | Security Zone |
| :--- | :--- | :---: | :--- |
| `GATE` | Main Gate & Security | $(0, 0)$ | `COMMON` |
| `ROUNDABOUT`| Central Roundabout Boulevard | $(0, 210)$ | `COMMON` |
| `CB` | Central Block (Sarvepalli Radhakrishnan) | $(150, 250)$ | `COMMON` |
| `AB2` | Dr. APJ Abdul Kalam Block | $(-130, 240)$ | `COMMON` |
| `ROCK_PLAZA` | Rock Plaza Amphitheatre (Tiered Steps) | $(-130, 290)$ | `COMMON` |
| `SAC` | Student Activity Centre | $(-130, 340)$ | `COMMON` |
| `CRESCENT` | Central Crescent Academic Block | $(0, 330)$ | `COMMON` |
| `FOOD_STREET`| Food Street & Dining Hub | $(10, 425)$ | `COMMON` |
| `LH1`, `LH3` | Ladies' Hostels (Cluster) | $(-170, 620)$ / $(-120, 560)$ | `LADIES_HOSTEL` |
| `MH1` - `MH6`| Men's Hostels (Cluster) | $(160 \text{ to } 330, 420 \text{ to } 600)$ | `MENS_HOSTEL` |

---

## 📁 Project Directory Structure

```text
📁 Project/
└── 📁 edu/
    └── 📁 vitap/
        └── 📁 navigation/
            ├── ☕ Main.java                      // System driver, CLI menu & benchmark suite
            │
            ├── 📦 model/
            │   ├── ☕ Node.java                  // Vertices with Cartesian (X,Y) coords & Security Zones
            │   ├── ☕ Edge.java                  // Attributed edges (metric distance, covered, stairs)
            │   └── ☕ RouteResult.java           // Telemetry container (cost, path, explored nodes)
            │
            ├── 📦 graph/
            │   ├── ☕ CampusGraph.java           // Adjacency List: Map<String, List<Edge>>
            │   └── ☕ CampusDataLoader.java      // Ground-truth VIT-AP spatial layout dataset
            │
            ├── 📦 algorithm/
            │   ├── ☕ PathFinder.java            // Unified routing strategy interface
            │   ├── ☕ EdgeWeightProvider.java    // Pluggable dynamic weight functional interface
            │   ├── ☕ DijkstraRouter.java        // Baseline router using uniform min-heap expansion
            │   └── ☕ AStarRouter.java           // Heuristic-driven optimal routing engine
            │
            └── 📦 ai/
                ├── ☕ NavigationContext.java     // Context state (Weather, TimeOfDay, Role, Mobility)
                ├── ☕ DynamicCostEngine.java     // Real-time edge cost modulation & zone filtering
                └── ☕ CampusQueryParser.java     // Conversational query entity & intent resolver
```

---

## ⚡ Getting Started

### Prerequisites
* **Java Development Kit (JDK):** Version 17 LTS or higher
* **Terminal / Console:** Any standard bash, zsh, or command prompt

### Compilation
From the project root directory:

```bash
# Create binary output directory
mkdir -p bin

# Compile all Java sources
javac -d bin $(find src -name "*.java")
```

*(On Windows PowerShell, use: `Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } | javac -d bin @args` or compile using your IDE).*

### Execution
Run the system console interface:

```bash
java -cp bin edu.vitap.navigation.Main
```

---

## 🎮 Interactive Modes & Usage

Upon launch, `Main.java` exposes three execution modes:

### Mode 1: Interactive Multi-Constraint Navigator
Allows manual step-by-step selection of:
1. **User Identity:** Male Student, Female Student, or Faculty/Staff.
2. **Origin & Destination:** Selected from all registered campus landmarks.
3. **Weather:** Clear Skies, Heavy Monsoon Rain, or Extreme Sun.
4. **Timing:** Regular Hours, Class Transition Rush Hour, or Night.
5. **Physical Mobility:** Standard (stairs allowed) or Step-Free (wheelchairs/crutches).

### Mode 2: Natural Language Prompt Query
Accepts natural English inputs and uses the NLP token matcher to infer parameters:
```text
> Enter query:
"Male student from Food Street to Kalam Block without stairs in heavy rain"

Resolved Entities:
  Source: FOOD_STREET
  Destination: AB2
  Extracted Context: [Role: MALE_STUDENT | Weather: RAIN | Timing: REGULAR | Avoid Stairs: true]
```

### Mode 3: Automated Benchmark Suite
Runs simultaneous routing between $A^*$ and Dijkstra over major campus pathways to measure node exploration count, execution path cost, and search space pruning percentage.

---

## 📊 Benchmarking Telemetry

Performance comparison across 4 benchmark routes at VIT-AP:

| Route Traversal Pair | Dijkstra Nodes Explored | $A^*$ Nodes Explored | Search Space Pruning |
| :--- | :---: | :---: | :---: |
| **Main Gate $\rightarrow$ Sports Ground** | 14 nodes | 7 nodes | **50.0%** |
| **Main Gate $\rightarrow$ MH3 (East Hostels)** | 17 nodes | 9 nodes | **47.1%** |
| **AB2 $\rightarrow$ MH4** | 16 nodes | 8 nodes | **50.0%** |
| **Crescent $\rightarrow$ LH1 (West Hostels)** | 12 nodes | 5 nodes | **58.3%** |
| **Average Improvement** | — | — | **51.35%** |

---

## 🎓 Academic Information

* **Course:** Data Structures and Algorithms (DSA)
* **Student:** Harsh
* **Registration Number:** 25BCA7202
* **Project Guide:** Dr. SAROJA KUMAR ROUT, Associate Professor
* **Department:** School of Computer Science and Engineering (SCOPE)
* **Institution:** VIT-AP University, Amaravati, Andhra Pradesh
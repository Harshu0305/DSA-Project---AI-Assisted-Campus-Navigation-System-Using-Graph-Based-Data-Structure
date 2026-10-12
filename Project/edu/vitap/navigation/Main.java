package edu.vitap.navigation;

import edu.vitap.navigation.ai.CampusQueryParser;
import edu.vitap.navigation.ai.CampusQueryParser.ParsedQuery;
import edu.vitap.navigation.ai.DynamicCostEngine;
import edu.vitap.navigation.ai.NavigationContext;
import edu.vitap.navigation.ai.NavigationContext.TimeOfDay;
import edu.vitap.navigation.ai.NavigationContext.UserRole;
import edu.vitap.navigation.ai.NavigationContext.Weather;
import edu.vitap.navigation.algorithm.AStarRouter;
import edu.vitap.navigation.algorithm.DijkstraRouter;
import edu.vitap.navigation.algorithm.PathFinder;
import edu.vitap.navigation.graph.CampusDataLoader;
import edu.vitap.navigation.graph.CampusGraph;
import edu.vitap.navigation.model.Node;
import edu.vitap.navigation.model.RouteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CampusGraph campus = CampusDataLoader.loadVITAPGraph();
        PathFinder aStar = new AStarRouter();
        PathFinder dijkstra = new DijkstraRouter();
        Scanner scanner = new Scanner(System.in);

        List<Node> landmarkList = new ArrayList<>(campus.getAllNodes());

        System.out.println("==================================================");
        System.out.println("  VIT-AP AI-ASSISTED CAMPUS NAVIGATION SYSTEM");
        System.out.println("  Features: A* Heuristics | Weather | Zone Security");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\nSelect Mode:");
            System.out.println("  [1] Interactive Multi-Constraint Navigator");
            System.out.println("  [2] AI Natural Language Prompt Query");
            System.out.println("  [3] Benchmark Suite (A* vs Dijkstra nodes explored)");
            System.out.println("  [0] Exit");
            System.out.print("Enter choice (0-3): ");
            int mode = readInt(scanner, 0, 3);

            if (mode == 0)
                break;

            if (mode == 1) {
                // 1. Role Selection
                System.out.println("\nSelect User Identity / Role:");
                System.out.println("  [1] Male Student (Restricted from Ladies Hostel)");
                System.out.println("  [2] Female Student (Restricted from Men's Hostel)");
                System.out.println("  [3] Faculty / Staff (Unrestricted Access)");
                System.out.print("Enter choice (1-3): ");
                int roleChoice = readInt(scanner, 1, 3);
                UserRole role = switch (roleChoice) {
                    case 1 -> UserRole.MALE_STUDENT;
                    case 2 -> UserRole.FEMALE_STUDENT;
                    default -> UserRole.STAFF_FACULTY;
                };

                // 2. Start Landmark
                System.out.println("\nSelect Current Location:");
                for (int i = 0; i < landmarkList.size(); i++) {
                    System.out.printf("  [%2d] %-35s [Zone: %s]\n", (i + 1), landmarkList.get(i).getName(),
                            landmarkList.get(i).getZone());
                }
                System.out.print("Enter choice (1-" + landmarkList.size() + "): ");
                int startIdx = readInt(scanner, 1, landmarkList.size());
                Node startNode = landmarkList.get(startIdx - 1);

                // 3. Destination Landmark
                System.out.println("\nSelect Destination:");
                for (int i = 0; i < landmarkList.size(); i++) {
                    if (i == startIdx - 1)
                        continue;
                    System.out.printf("  [%2d] %-35s [Zone: %s]\n", (i + 1), landmarkList.get(i).getName(),
                            landmarkList.get(i).getZone());
                }
                System.out.print("Enter choice (1-" + landmarkList.size() + "): ");
                int endIdx = readInt(scanner, 1, landmarkList.size());
                while (endIdx == startIdx) {
                    System.out.print("Destination cannot be start location. Choose again: ");
                    endIdx = readInt(scanner, 1, landmarkList.size());
                }
                Node targetNode = landmarkList.get(endIdx - 1);

                // 4. Weather Selection
                System.out.println("\nCurrent Weather:");
                System.out.println("  [1] Clear");
                System.out.println("  [2] Heavy Rain / Monsoon (Penalizes outdoor; prefers covered skyway)");
                System.out.println("  [3] Extreme Sun / Afternoon Heat (Penalizes unshaded sports fields)");
                System.out.print("Enter choice (1-3): ");
                Weather weather = switch (readInt(scanner, 1, 3)) {
                    case 2 -> Weather.RAIN;
                    case 3 -> Weather.EXTREME_SUN;
                    default -> Weather.CLEAR;
                };

                // 5. Timing Selection
                System.out.println("\nTiming / Class Schedule:");
                System.out.println("  [1] Regular Hours");
                System.out.println("  [2] Slot Change Rush Hour (Academic block bottleneck)");
                System.out.println("  [3] Late Night");
                System.out.print("Enter choice (1-3): ");
                TimeOfDay timeOfDay = switch (readInt(scanner, 1, 3)) {
                    case 2 -> TimeOfDay.RUSH_HOUR;
                    case 3 -> TimeOfDay.NIGHT;
                    default -> TimeOfDay.REGULAR;
                };

                // 6. Step-Free Access
                System.out.println("\nAccessibility Preferences:");
                System.out.println("  [1] Standard (Stairs allowed)");
                System.out.println("  [2] Step-Free / Wheelchair / Crutches (Bypasses Rock Plaza steps)");
                System.out.print("Enter choice (1-2): ");
                boolean avoidStairs = (readInt(scanner, 1, 2) == 2);

                NavigationContext context = new NavigationContext(weather, timeOfDay, avoidStairs, role);
                DynamicCostEngine costEngine = new DynamicCostEngine(context);

                System.out.println("\n--------------------------------------------------");
                System.out.printf("Routing: %s -> %s\n", startNode.getName(), targetNode.getName());
                System.out.println("Context: " + context);
                System.out.println("--------------------------------------------------");

                RouteResult result = aStar.findRoute(campus, startNode.getId(), targetNode.getId(), costEngine);
                result.printSummary("A* Search with AI Constraints");

            } else if (mode == 2) {
                System.out.print(
                        "\nEnter your navigation query (e.g., 'Male student from Food Street to LH3 without stairs in rain'):\n> ");
                scanner.nextLine(); // Clear newline
                String query = scanner.nextLine().trim();

                ParsedQuery parsed = CampusQueryParser.parse(query);
                System.out.println("\nResolved Entities:");
                System.out.println("  Source: " + parsed.getStartNodeId());
                System.out.println("  Destination: " + parsed.getTargetNodeId());
                System.out.println("  Extracted Context: " + parsed.getContext());

                if (parsed.isValid()) {
                    DynamicCostEngine costEngine = new DynamicCostEngine(parsed.getContext());
                    RouteResult result = aStar.findRoute(campus, parsed.getStartNodeId(), parsed.getTargetNodeId(),
                            costEngine);
                    result.printSummary("A* Search (NLP Parsed)");
                } else {
                    System.out.println("Error: Could not extract valid start and destination from input prompt.");
                }

            } else if (mode == 3) {
                System.out.println("\n--- RUNNING ALGORITHMIC BENCHMARK SUITE ---");
                String[][] testPairs = {
                        { "GATE", "SPORTS_GROUND" },
                        { "GATE", "MH3" },
                        { "AB2", "MH4" },
                        { "CRESCENT", "LH1" }
                };

                for (String[] pair : testPairs) {
                    RouteResult dRes = dijkstra.findRoute(campus, pair[0], pair[1]);
                    RouteResult aRes = aStar.findRoute(campus, pair[0], pair[1]);

                    System.out.printf("\nRoute: %s -> %s\n", pair[0], pair[1]);
                    System.out.printf("  Dijkstra Explored Nodes : %d (Cost: %.2fm)\n", dRes.getNodesExplored(),
                            dRes.getTotalCost());
                    System.out.printf("  A* Explored Nodes       : %d (Cost: %.2fm)\n", aRes.getNodesExplored(),
                            aRes.getTotalCost());
                    double reduction = ((double) (dRes.getNodesExplored() - aRes.getNodesExplored())
                            / dRes.getNodesExplored()) * 100.0;
                    System.out.printf("  Search Space Reduction  : %.1f%%\n", reduction);
                }
            }
        }

        System.out.println("\nCampus Navigation System closed. Have a safe walk!");
        scanner.close();
    }

    private static int readInt(Scanner scanner, int min, int max) {
        while (true) {
            if (scanner.hasNextInt()) {
                int val = scanner.nextInt();
                if (val >= min && val <= max) {
                    return val;
                }
            } else {
                scanner.next();
            }
            System.out.printf("Invalid input. Enter a number between %d and %d: ", min, max);
        }
    }
}
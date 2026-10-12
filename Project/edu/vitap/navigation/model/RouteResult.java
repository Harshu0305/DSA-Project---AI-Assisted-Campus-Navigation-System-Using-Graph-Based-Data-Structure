package edu.vitap.navigation.model;

import java.util.Collections;
import java.util.List;

public class RouteResult {
    private final List<Node> path;
    private final double totalCost;
    private final int nodesExplored;
    private final boolean found;

    public RouteResult(List<Node> path, double totalCost, int nodesExplored, boolean found) {
        this.path = path != null ? path : Collections.emptyList();
        this.totalCost = totalCost;
        this.nodesExplored = nodesExplored;
        this.found = found;
    }

    public static RouteResult failure(int nodesExplored) {
        return new RouteResult(Collections.emptyList(), Double.POSITIVE_INFINITY, nodesExplored, false);
    }

    public List<Node> getPath() {
        return path;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public int getNodesExplored() {
        return nodesExplored;
    }

    public boolean isFound() {
        return found;
    }

    public void printSummary(String algorithmName) {
        System.out.println("==================================================");
        System.out.println("Algorithm: " + algorithmName);
        System.out.println("Status: " + (found ? "Path Found" : "No Path Found / Blocked by Zone Restrictions"));
        if (found) {
            System.out.printf("Estimated Travel Cost / Distance: %.2f meters\n", totalCost);
            System.out.println("Search Frontier Nodes Explored: " + nodesExplored);
            System.out.println("Calculated Path:");
            for (int i = 0; i < path.size(); i++) {
                Node node = path.get(i);
                System.out.printf("  %d. %s [%s] - Zone: %s\n", (i + 1), node.getName(), node.getId(), node.getZone());
            }
        }
        System.out.println("==================================================");
    }
}
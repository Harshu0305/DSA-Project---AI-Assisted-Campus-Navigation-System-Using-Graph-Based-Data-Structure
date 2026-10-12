package edu.vitap.navigation.model;

public class Edge {
    private final Node source;
    private final Node target;
    private final double baseDistance;
    private final boolean hasStairs;
    private final boolean isIndoor;

    public Edge(Node source, Node target, double baseDistance, boolean hasStairs, boolean isIndoor) {
        this.source = source;
        this.target = target;
        this.baseDistance = baseDistance;
        this.hasStairs = hasStairs;
        this.isIndoor = isIndoor;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public double getBaseDistance() {
        return baseDistance;
    }

    public boolean hasStairs() {
        return hasStairs;
    }

    public boolean isIndoor() {
        return isIndoor;
    }
}
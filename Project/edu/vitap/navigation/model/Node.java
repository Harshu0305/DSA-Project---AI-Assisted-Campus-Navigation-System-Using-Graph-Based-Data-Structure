package edu.vitap.navigation.model;

public class Node {
    public enum Zone {
        COMMON, // Accessible to everyone (Academic blocks, Food Street, Central Roundabout)
        LADIES_HOSTEL, // LH cluster (Restricted to female students & staff)
        MENS_HOSTEL // MH cluster (Restricted to male students & staff)
    }

    private final String id;
    private final String name;
    private final double x;
    private final double y;
    private final String category;
    private final Zone zone;

    public Node(String id, String name, double x, double y, String category, Zone zone) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.category = category;
        this.zone = zone;
    }

    public Node(String id, String name, double x, double y, String category) {
        this(id, name, x, y, category, Zone.COMMON);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getCategory() {
        return category;
    }

    public Zone getZone() {
        return zone;
    }

    public double distanceTo(Node other) {
        return Math.hypot(this.x - other.x, this.y - other.y);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [%s]", name, id, zone);
    }
}
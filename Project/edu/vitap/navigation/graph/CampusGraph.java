package edu.vitap.navigation.graph;

import edu.vitap.navigation.model.Edge;
import edu.vitap.navigation.model.Node;

import java.util.*;

public class CampusGraph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(String fromId, String toId, double distance, boolean hasStairs, boolean isIndoor) {
        Node from = nodes.get(fromId);
        Node to = nodes.get(toId);

        if (from == null || to == null) {
            throw new IllegalArgumentException("Both nodes must exist in graph: " + fromId + " -> " + toId);
        }

        adjacencyList.get(fromId).add(new Edge(from, to, distance, hasStairs, isIndoor));
        adjacencyList.get(toId).add(new Edge(to, from, distance, hasStairs, isIndoor));
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public Collection<Node> getAllNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public List<Edge> getNeighbors(String nodeId) {
        return adjacencyList.getOrDefault(nodeId, Collections.emptyList());
    }

    public int getNodeCount() {
        return nodes.size();
    }
}
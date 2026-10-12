package edu.vitap.navigation.algorithm;

import edu.vitap.navigation.graph.CampusGraph;
import edu.vitap.navigation.model.Edge;
import edu.vitap.navigation.model.Node;
import edu.vitap.navigation.model.RouteResult;

import java.util.*;

public class DijkstraRouter implements PathFinder {

    private static class SearchNode implements Comparable<SearchNode> {
        final Node node;
        final double distance;

        SearchNode(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(SearchNode other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    @Override
    public RouteResult findRoute(CampusGraph graph, String startId, String targetId) {
        return findRoute(graph, startId, targetId, EdgeWeightProvider.DEFAULT);
    }

    @Override
    public RouteResult findRoute(CampusGraph graph, String startId, String targetId,
            EdgeWeightProvider weightProvider) {
        Node start = graph.getNode(startId);
        Node target = graph.getNode(targetId);

        if (start == null || target == null) {
            return RouteResult.failure(0);
        }

        Map<String, Double> minDistance = new HashMap<>();
        Map<String, Node> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();

        minDistance.put(start.getId(), 0.0);
        pq.add(new SearchNode(start, 0.0));

        int nodesExplored = 0;

        while (!pq.isEmpty()) {
            SearchNode current = pq.poll();
            String currentId = current.node.getId();

            if (visited.contains(currentId)) {
                continue;
            }
            visited.add(currentId);
            nodesExplored++;

            if (currentId.equals(targetId)) {
                return buildResult(parentMap, start, target, minDistance.get(targetId), nodesExplored);
            }

            for (Edge edge : graph.getNeighbors(currentId)) {
                Node neighbor = edge.getTarget();
                if (visited.contains(neighbor.getId())) {
                    continue;
                }

                double weight = weightProvider.getCost(edge);
                if (Double.isInfinite(weight)) {
                    continue; // Skip impassable or restricted edges
                }

                double newDist = minDistance.get(currentId) + weight;
                if (newDist < minDistance.getOrDefault(neighbor.getId(), Double.POSITIVE_INFINITY)) {
                    minDistance.put(neighbor.getId(), newDist);
                    parentMap.put(neighbor.getId(), current.node);
                    pq.add(new SearchNode(neighbor, newDist));
                }
            }
        }

        return RouteResult.failure(nodesExplored);
    }

    private RouteResult buildResult(Map<String, Node> parents, Node start, Node target, double cost, int explored) {
        List<Node> path = new ArrayList<>();
        Node curr = target;

        while (curr != null) {
            path.add(curr);
            curr = parents.get(curr.getId());
        }
        Collections.reverse(path);
        return new RouteResult(path, cost, explored, true);
    }
}
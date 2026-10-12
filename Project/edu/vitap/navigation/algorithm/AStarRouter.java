package edu.vitap.navigation.algorithm;

import edu.vitap.navigation.graph.CampusGraph;
import edu.vitap.navigation.model.Edge;
import edu.vitap.navigation.model.Node;
import edu.vitap.navigation.model.RouteResult;

import java.util.*;

public class AStarRouter implements PathFinder {

    private static class AStarNode implements Comparable<AStarNode> {
        final Node node;
        final double fScore;

        AStarNode(Node node, double fScore) {
            this.node = node;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(AStarNode other) {
            return Double.compare(this.fScore, other.fScore);
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

        Map<String, Double> gScore = new HashMap<>();
        Map<String, Node> parentMap = new HashMap<>();
        Set<String> closedSet = new HashSet<>();
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();

        gScore.put(start.getId(), 0.0);
        openSet.add(new AStarNode(start, start.distanceTo(target)));

        int nodesExplored = 0;

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            String currentId = current.node.getId();

            if (closedSet.contains(currentId)) {
                continue;
            }
            closedSet.add(currentId);
            nodesExplored++;

            if (currentId.equals(targetId)) {
                return buildResult(parentMap, start, target, gScore.get(targetId), nodesExplored);
            }

            for (Edge edge : graph.getNeighbors(currentId)) {
                Node neighbor = edge.getTarget();
                if (closedSet.contains(neighbor.getId())) {
                    continue;
                }

                double weight = weightProvider.getCost(edge);
                if (Double.isInfinite(weight)) {
                    continue; // Skip impassable or restricted edges
                }

                double tentativeG = gScore.get(currentId) + weight;

                if (tentativeG < gScore.getOrDefault(neighbor.getId(), Double.POSITIVE_INFINITY)) {
                    parentMap.put(neighbor.getId(), current.node);
                    gScore.put(neighbor.getId(), tentativeG);

                    double h = neighbor.distanceTo(target);
                    openSet.add(new AStarNode(neighbor, tentativeG + h));
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
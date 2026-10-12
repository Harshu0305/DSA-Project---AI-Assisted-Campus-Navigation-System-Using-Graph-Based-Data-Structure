package edu.vitap.navigation.algorithm;

import edu.vitap.navigation.graph.CampusGraph;
import edu.vitap.navigation.model.RouteResult;

public interface PathFinder {
    RouteResult findRoute(CampusGraph graph, String startId, String targetId);

    RouteResult findRoute(CampusGraph graph, String startId, String targetId, EdgeWeightProvider weightProvider);
}
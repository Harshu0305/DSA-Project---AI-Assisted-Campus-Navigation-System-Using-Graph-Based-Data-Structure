package edu.vitap.navigation.algorithm;

import edu.vitap.navigation.model.Edge;

@FunctionalInterface
public interface EdgeWeightProvider {
    double getCost(Edge edge);

    EdgeWeightProvider DEFAULT = Edge::getBaseDistance;
}
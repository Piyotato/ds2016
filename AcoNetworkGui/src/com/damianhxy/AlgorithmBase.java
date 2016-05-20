package com.damianhxy;

import javafx.util.*;

/**
 * Created by damian on 17/5/16.
 */
abstract class AlgorithmBase {
    /* Todo: Add option to speedup simulation? */
    int numNodes, currentTime;
    final int source, destination;

    /**
     * Initialize algorithm
     *
     * @param _source Source node
     * @param _destination Destination node
     */
    AlgorithmBase(int _source, int _destination) {
        source = _source;
        destination = _destination;
    }

    abstract void addNode(int speed);

    abstract void toggleNode(int ID);

    abstract void addEdge(int node1, int node2, int cost);

    abstract void toggleEdge(int ID);

    abstract Pair<Integer, Integer> tick();
}

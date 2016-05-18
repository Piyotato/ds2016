package com.damianhxy;

import javafx.util.*;

/**
 * Created by damian on 17/5/16.
 */
abstract class AlgoBase {

    final static int SIM_SPEED = 1; // Simulated Time : Real Time
    int numNodes, currentTime;
    final int source, destination;

    /**
     * Initialize algorithm
     *
     * @param _source Source node
     * @param _destination Destination node
     */
    AlgoBase(int _source, int _destination) {
        source = _source;
        destination = _destination;
    }

    abstract void addNode();

    abstract void toggleNode(int ID);

    abstract void addEdge(int node1, int node2, int cost);

    abstract void toggleEdge(int ID);

    abstract Pair<Integer, Integer> tick();
}

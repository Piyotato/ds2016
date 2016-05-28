package com.damianhxy;

import javafx.util.*;

/**
 * Created by damian on 17/5/16.
 */
abstract class AlgorithmBase {

    int currentTime;
    final int source, destination;

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

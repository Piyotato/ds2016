package com.ds2016;

import java.util.ArrayDeque;

/**
 * Created by damian on 16/5/16.
 */
class Edge_ACO extends Edge {

    final ArrayDeque<Ant> ants = new ArrayDeque<>();

    /**
     * Initializes an edge
     *
     * @param _source Start node
     * @param _destination End node
     * @param _cost Time taken to traverse
     */
    Edge_ACO(int _source, int _destination, int _cost) {
        super(_source, _destination, _cost);
    }

    /**
     * Transmit an ant
     *
     * @param ant Ant
     * @param currentTime Timestamp
     */
    void addAnt(Ant ant, int currentTime) {
        ant.timestamp = currentTime + cost;
        ant.totalTime += cost;
        ants.add(ant);
    }
}

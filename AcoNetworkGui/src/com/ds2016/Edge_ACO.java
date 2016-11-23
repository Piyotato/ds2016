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
     * @param source      Start node
     * @param destination End node
     * @param cost        Time taken to traverse
     * @param bandwidth   Packets per tick
     */
    Edge_ACO(int source, int destination, int cost, int bandwidth) {
        super(source, destination, cost, bandwidth);
    }

    /**
     * Transmit an ant
     *
     * @param ant         Ant
     * @param currentTime Timestamp
     */
    void addAnt(Ant ant, int currentTime) {
        ant.timestamp = currentTime + cost;
        ants.add(ant);
    }

    /**
     * Clear ant queue
     */
    void clearAntQ() {
        ants.clear();
    }
}

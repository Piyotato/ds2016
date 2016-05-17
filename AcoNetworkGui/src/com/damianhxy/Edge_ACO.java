package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Edge_ACO {

    int source, destination, cost;
    boolean isOffline;
    Queue<Ant> ant;
    Queue<Packet> packet;

    /**
     * Initializes an edge
     *
     * @param _source Startpoint
     * @param _destination Endpoint
     * @param _cost Time taken to traverse
     */
    Edge_ACO(int _source, int _destination, int _cost) {
        source = _source;
        destination = _destination;
        cost = _cost;
    }

    /**
     * Updates distance
     *
     * @param _cost Time taken to traverse
     */
    void updateCost(int _cost) {
        cost = _cost;
    }

    /**
     * Transmit an ant
     *
     * @param A Ant
     * @param currentTime Timestamp
     */
    void addAnt(Ant A, int currentTime) {
        A.timestamp = currentTime + cost;
        A.totalTime += cost;
        ant.add(A);
    }

    /**
     * Transmit a packet
     *
     * @param P Packet
     * @param currentTime Timestamp
     */
    void addPacket(Packet P, int currentTime) {
        P.timestamp = currentTime + cost;
        packet.add(P);
    }
}

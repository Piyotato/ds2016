package com.damianhxy;

import java.util.*;
import javafx.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {

    boolean isOffline;
    UFDS DSU;
    private ArrayList<ArrayList<Double>> pheromone; // Node, Destination
    private ArrayList<ArrayList<Integer>> adjList;
    private ArrayList<Edge_ACO> edgeList;
    private int numNodes;
    Queue<Ant> fastQ;
    Queue<Packet> slowQ;
    int speed, ID;

    /**
     * Initialise a node
     *
     * @param _ID Unique Identifier
     */
    Node_EACO(int _ID) {
        ID = _ID;
    }

    /**
     * Initialize the UFDS structure
     */
    private void initDSU() {
        DSU = new UFDS(numNodes);
        for (Edge_ACO edge: edgeList) {
            if (edge.source == ID || edge.destination == ID) continue;
            if (edge.isOffline) continue;
            DSU.unionSet(edge.source, edge.destination);
        }
    }

    /**
     * Use heuristics to calculate the
     * next best hop, for a given destination
     *
     * @param destination Destination node
     * @param alpha Weightage of pheromone
     * @param beta Weightage of cost
     * @return Neighbour for next hop
     */
    int nextHop(int destination, int alpha, int beta) {
        double RNG = Math.random();
        /* Stuff */
    }

    /* Todo: Implement actual updates? */

    /**
     * Process (propagated) updates to generate
     * an up-to-date copy of the system topology
     */
    void update() {
        /* Stuff */
    }
}

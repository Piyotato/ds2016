package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {

    boolean isOffline;
    UFDS DSU;
    private ArrayList<ArrayList<Double>> pheromone; // Node, Destination
    private ArrayList<Boolean> nodes; // Is offline
    private ArrayList<SimpleEdge> edgeList;
    int numNodes;
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
            if (nodes.get(edge.source) || nodes.get(edge.destination)) continue;
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

    /* Todo: Implement actual updates */

    /* Todo: Update pheromone function */

    /**
     * Add a new node
     */
    void addNode() {
        nodes.add(false);
        ++numNodes;
    }

    /**
     * Toggle state of a node
     *
     * @param ID Node ID
     */
    void toggleNode(int ID) {
        nodes.set(ID, !nodes.get(ID));
        update();
    }

    /**
     * Add a bidirectional edge
     *
     * @param node1 First Node
     * @param node2 Second Node
     * @param cost Time Taken
     */
    void addEdge(int node1, int node2, int cost) {
        edgeList.add(new SimpleEdge(node1, node2, cost));
        edgeList.add(new SimpleEdge(node2, node1, cost));
        DSU.unionSet(node1, node2);
    }

    /**
     * Toggle state of en edge
     *
     * @param ID Edge ID
     */
    void toggleEdge(int ID) {
        SimpleEdge edge = edgeList.get(ID);
        edge.isOffline ^= true;
        if (edge.isOffline) {
            update();
        } else {
            DSU.unionSet(edge.source, edge.destination);
        }
    }

    /**
     * Update DSU and heuristic
     */
    private void update() {
        initDSU();
        /* Todo: efficiently update viability */
    }
}

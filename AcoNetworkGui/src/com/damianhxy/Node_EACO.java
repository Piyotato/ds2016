package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {
    /* Todo: Resize pheromone table or add to object */
    boolean isOffline;
    private UFDS DSU;
    private ArrayList<ArrayList<Double>> pheromone; // Node, Destination
    private ArrayList<Boolean> nodes; // Is offline
    private ArrayList<SimpleEdge> edgeList, neighbours;
    int numNodes;
    Queue<Ant> fastQ;
    Queue<Packet> slowQ;
    int speed, NODEID;

    /**
     * Initialise a node
     *
     * @param _NODEID Unique Identifier
     * @param _edgeList Initial topology
     */
    Node_EACO(int _NODEID, ArrayList<Edge_ACO> _edgeList) {
        /* Todo: Initialise topology */
        numNodes = NODEID = _NODEID;
        /* Todo: Initialise pheromone table */
    }

    /**
     * Initialize the UFDS structure
     */
    private void initDSU() {
        neighbours.clear();
        DSU = new UFDS(numNodes);
        for (SimpleEdge edge: edgeList) {
            if (edge.source == NODEID)
                neighbours.add(edge);
            if (edge.source == NODEID || edge.destination == NODEID) continue;
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
    int nextHop(int destination, int alpha, int beta) throws IllegalStateException {
        double RNG = Math.random();
        double totval = .0;
        for (SimpleEdge edge: neighbours) {
            totval += Math.pow(pheromone.get(edge.destination).get(destination), alpha) * Math.pow(edge.cost, beta);
        }
        for (SimpleEdge edge: neighbours) {
            double val = Math.pow(pheromone.get(edge.destination).get(destination), alpha) * Math.pow(edge.cost, beta);
            RNG -= val / totval;
            if (RNG <= 0) return edge.destination;
        }
        throw new IllegalStateException();
    }

    /* Todo: Implement actual updates? */

    /**
     * Add a new node
     */
    void addNode() {
        /* Todo: Update pheromone table size */
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
        if (nodes.get(ID)) {
            update(true);
        } else {
            for (SimpleEdge edge: edgeList) {
                if (edge.source != ID && edge.destination != ID) continue;
                if (!edge.isOffline) {
                    /* Todo: Add to neighbours if needed */
                    DSU.unionSet(edge.source, edge.destination);
                }
            }
            update(false);
        }
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
        /* Todo: Add to neighbours if needed */
        DSU.unionSet(node1, node2);
        update(false);
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
            update(true);
        } else {
            /* Todo: Add to neighbours if needed */
            DSU.unionSet(edge.source, edge.destination);
            update(false);
        }
    }

    /**
     * Update DSU and heuristic
     *
     * @param full Whether a complete rebuild is needed
     */
    private void update(boolean full) {
        if (full) {
            initDSU();
        }
        /* Todo: efficiently update viability */
    }

    /**
     * Change the value by a certain amount
     * Modifies other values as needed to
     * achieve a sum of one
     *
     * @param neighbour ID of neighbour
     * @param destination ID of destination
     * @param change Pheromone change
     */
    private void updateHeuristics(int neighbour, int destination, double change) {

    }
}

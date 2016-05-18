package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {

    boolean isOffline;
    private UFDS DSU;
    private ArrayList<ArrayList<Double>> pheromone; // Node, Destination
    private ArrayList<ArrayList<SimpleEdge>> adjList;
    private ArrayList<Boolean> nodes; // Is offline?
    private ArrayList<SimpleEdge> edgeList;
    private int numNodes;
    Queue<Ant> fastQ;
    Queue<Packet> slowQ;
    int speed, NODEID;
    /* Todo: Implement actual *propagated* updates */
    /**
     * Initialise a node
     *
     * @param _NODEID Unique Identifier
     * @param _edgeList Initial topology
     */
    Node_EACO(int _NODEID, ArrayList<Edge_ACO> _edgeList) {
        for (Edge_ACO edge: _edgeList) {
            edgeList.add(new SimpleEdge(edge.source, edge.destination, edge.cost));
        }
        numNodes = NODEID = _NODEID;
        for (int a = 0; a < numNodes; ++a) {
            pheromone.add(new ArrayList<>(numNodes));
        }
    }

    /**
     * Initialize the UFDS structure
     */
    private void initDSU() {
        DSU = new UFDS(numNodes);
        for (SimpleEdge edge: edgeList) {
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
     * @param alpha Weightage of pheromone
     * @param beta Weightage of cost
     * @param tabuSize Size of Taub list
     * @return Neighbour for next hop
     */
    int nextHop(Packet packet, int alpha, int beta, int tabuSize) throws IllegalStateException {
        double RNG = Math.random();
        double totVal = .0;
        boolean anyValid = false;
        for (SimpleEdge edge: adjList.get(packet.source)) {
            if (edge.isOffline) continue;
            if (nodes.get(edge.destination)) continue;
            if (!packet.isValid(edge.destination, tabuSize)) continue;
            anyValid = true;
            totVal += Math.pow(pheromone.get(edge.destination).get(packet.destination), alpha) * Math.pow(edge.cost, beta);
        }
        if (!anyValid) return -1;
        for (SimpleEdge edge: adjList.get(packet.source)) {
            if (edge.isOffline) continue;
            if (nodes.get(edge.destination)) continue;
            if (!packet.isValid(edge.destination, tabuSize)) continue;
            double val = Math.pow(pheromone.get(edge.destination).get(packet.destination), alpha) * Math.pow(edge.cost, beta);
            RNG -= val / totVal;
            if (RNG <= 0) return edge.destination;
        }
        throw new IllegalStateException();
    }

    /**
     * Add a new node
     */
    void addNode() {
        for (ArrayList<Double> node: pheromone) {
            node.add(.0);
        }
        pheromone.add(new ArrayList<>(++numNodes));
        adjList.add(new ArrayList<>());
        nodes.add(false);
    }

    /**
     * Toggle state of a node
     *
     * @param ID Node ID
     */
    void toggleNode(int ID) {
        nodes.set(ID, !nodes.get(ID));
        if (nodes.get(ID)) {
            update(-1);
        } else {
            for (SimpleEdge edge: edgeList) {
                if (edge.source != ID) continue;
                if (!edge.isOffline) {
                    DSU.unionSet(edge.source, edge.destination);
                }
            }
            update(ID);
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
        SimpleEdge forward = new SimpleEdge(node1, node2, cost);
        SimpleEdge backward = new SimpleEdge(node2, node1, cost);
        edgeList.add(forward);
        edgeList.add(backward);
        adjList.get(node1).add(forward);
        adjList.get(node2).add(backward);
        DSU.unionSet(node1, node2);
        update(node2);
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
            update(-1);
        } else {
            DSU.unionSet(edge.source, edge.destination);
            update(edge.destination);
        }
    }

    /**
     * Update DSU and heuristic
     *
     * @param node Updated node, or -1 if full rebuild
     */
    private void update(int node) {
        if (node == -1) { // Full update
            initDSU();
        }
        /* Find affected neighbours & destinations */
        ArrayList<SimpleEdge> neighbours, destinations;
        /* Todo: efficiently update viability */
        for (SimpleEdge edge: adjList.get(NODEID)) {

        }
        /* Todo: Wrap a bidirectional edge class? */
        for (SimpleEdge edge: edgeList) {

        }
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
    void updateHeuristic(int neighbour, int destination, double change) {

    }

    /**
     * Remove a neighbour from consideration
     * as it is inviable now
     *
     * @param neighbour Node ID
     */
    void addHeuristic(int neighbour) {
        /* Add neighbour from neighbours */
        /* Add heuristic value */
    }

    /**
     * Add a neighbour to consideration
     * as it is viable now
     *
     * @param neighbour Node ID
     */
    void removeHeuristic(int neighbour) {
        /* Remove neighbour from neighbours */
        /* Remove heuristic value */
    }
}

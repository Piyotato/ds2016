package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {

    final static double EPS = 1e-5;

    boolean isOffline;
    private UFDS DSU;
    private ArrayList<ArrayList<Double>> pheromone = new ArrayList<>(); // Destination, Node
    private ArrayList<ArrayList<SimpleEdge>> adjList = new ArrayList<>();
    private ArrayList<Boolean> nodes = new ArrayList<>(); // Is offline?
    private ArrayList<SimpleEdge> edgeList = new ArrayList<>();
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
        SimpleEdge last = null;
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
            last = edge;
            double val = Math.pow(pheromone.get(edge.destination).get(packet.destination), alpha) * Math.pow(edge.cost, beta);
            RNG -= val / totVal;
            if (RNG <= 0) return edge.destination;
        }
        if (last == null) throw new IllegalStateException();
        return last.destination;
    }

    /**
     * Add a new node
     */
    void addNode() {
        for (ArrayList<Double> node: pheromone) {
            node.add(.0);
        }
        pheromone.add(new ArrayList<>(++numNodes));
        pheromone.get(numNodes).set(numNodes, 1.); /* Destination is itself */
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
        ArrayList<SimpleEdge> neighbours = new ArrayList<>();
        ArrayList<Integer> destinations = new ArrayList<>();
        for (SimpleEdge edge: adjList.get(node)) { // Each neighbour
            if (DSU.sameSet(node, edge.destination)) {
                neighbours.add(edge);
            }
        }
        for (int a = 0; a < numNodes; ++a) {
            if (a == NODEID) continue;
            if (DSU.sameSet(a, node)) {
                destinations.add(a);
            }
        }
        for (SimpleEdge edge: neighbours) {
            for (Integer dest: destinations) {
                Double prev = pheromone.get(dest).get(edge.destination);
                if (prev == null) { // Previously inviable
                    if (DSU.sameSet(edge.destination, dest)) // Now viable
                        addHeuristic(edge.destination, dest);
                } else { // Previously viable
                    if (!DSU.sameSet(edge.destination, dest)) // Now inviable
                        removeHeuristic(edge.destination, dest);
                }
            }
        }
    }

    /**
     * Change the value by a certain amount
     * Modifies other values proportionally to
     * achieve a sum of one
     *
     * @param neighbour ID of neighbour
     * @param destination ID of destination
     * @param change Pheromone change
     */
    void updateHeuristic(int neighbour, int destination, double change) throws IllegalArgumentException {
        double tot = .0;
        ArrayList<Double> dest = pheromone.get(destination);
        /* Change Value */
        if (change < 0) { /* dest.get(neighbour) != null */
            if (Math.abs(dest.get(neighbour) - change) < EPS) {
                dest.set(neighbour, null);
            } else {
                dest.set(neighbour, dest.get(neighbour) + change);
                if (dest.get(neighbour) < 0) throw new IllegalArgumentException();
            }
        } else {
            dest.set(neighbour, dest.get(neighbour) + change);
            if (dest.get(neighbour) > 1) throw new IllegalArgumentException();
        }
        /* Calculate other sum */
        for (int a = 0; a < dest.size(); ++a) {
            if (a == neighbour) continue;
            if (dest.get(a) != null) {
                tot += dest.get(a);
            }
        }
        /* Decrease proportionally */
        for (int a = 0; a < dest.size(); ++a) {
            if (a == neighbour) continue;
            if (dest.get(a) != null) {
                dest.set(a, dest.get(a) * (1 + change / tot));
            }
        }
    }

    /**
     * Add a neighbour to consideration
     * as it is viable now
     *
     * @param neighbour Node ID
     * @param destination Destination ID
     */
    void addHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, 1. / viableNeighbours(destination));
    }

    /**
     * Remove a neighbour from consideration
     * as it is inviable now
     *
     * @param neighbour Node ID
     * @param destination Destination ID
     */
    void removeHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, -pheromone.get(neighbour).get(destination));
    }

    /**
     * Counts viable neighbours
     * for a given destination
     *
     * @param destination ID of destination
     * @return Number of viable neighbours
     */
    private int viableNeighbours(int destination) {
        int cnt = 0;
        for (SimpleEdge edge: adjList.get(NODEID)) {
            if (edge.isOffline) continue;
            if (DSU.sameSet(edge.destination, destination)) ++cnt;
        }
        return cnt;
    }
}

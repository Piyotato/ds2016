package com.damianhxy;

import java.util.*;
import javafx.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {

    private final static double EPS = 1e-5;

    boolean isOffline;
    private UFDS DSU;
    final HashMap2D<Integer, Integer, Double> pheromone = new HashMap2D<>(); // Destination, Node
    private final ArrayList<ArrayList<SimpleEdge>> adjList;
    private final ArrayList<Boolean> nodes = new ArrayList<>(); // Is offline?
    private final ArrayList<SimpleEdge> edgeList = new ArrayList<>();
    private int numNodes;
    final int nodeID, speed;
    final Queue<Ant> fastQ = new ArrayDeque<>();
    final Queue<Packet> slowQ = new ArrayDeque<>();

    /* Todo: Implement actual *propagated* updates */
    /**
     * Initialise a node
     *
     * @param _nodeID Unique Identifier
     * @param _speed Processing speed
     * @param _nodes Initial status of nodes
     * @param _edgeList Initial topology
     */

    Node_EACO(int _nodeID, int _speed, ArrayList<Node_EACO> _nodes, ArrayList<Edge_ACO> _edgeList) {
        speed = _speed;
        for (Edge_ACO edge: _edgeList) {
            edgeList.add(new SimpleEdge(edge.source, edge.destination, edge.cost));
        }
        for (Node_EACO node: _nodes) {
            nodes.add(node.isOffline);
        }
        numNodes = nodeID = _nodeID;
        adjList = new ArrayList<>(numNodes);
    }

    /**
     * Initialize the UFDS structure
     */
    private void initDSU() {
        DSU = new UFDS(numNodes);
        for (SimpleEdge edge: edgeList) {
            if (edge.source == nodeID || edge.destination == nodeID) continue;
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
     * @param tabuSize Size of Tabu list
     * @return Neighbour for next hop, or null if cycle exists
     */
    Integer nextHop(Packet packet, int alpha, int beta, int tabuSize) {
        double RNG = Math.random();
        double totVal = .0;
        ArrayList<Pair<Integer, Double>> neighbours = new ArrayList<>(); // Neighbour, Heuristic
        for (SimpleEdge edge: adjList.get(packet.source)) {
            if (edge.isOffline) continue; // Link is offline
            if (nodes.get(edge.destination)) continue; // Node is offline
            if (!packet.isValid(edge.destination, tabuSize)) continue; // Cycle detection
            Double tau = pheromone.get(packet.destination, edge.destination); // Pheromone
            Double eta = 1. / edge.cost; // 1 / Distance
            if (tau == null) continue; // Not viable
            neighbours.add(new Pair<>(edge.destination, Math.pow(tau, alpha) * Math.pow(eta, beta)));
            totVal += neighbours.get(neighbours.size() - 1).getValue();
        }
        if (neighbours.isEmpty()) return null;
        for (Pair<Integer, Double> neighbour: neighbours) {
            RNG -= neighbour.getValue() / totVal;
            if (RNG <= EPS) return neighbour.getKey();
        }
        return neighbours.get(neighbours.size() - 1).getKey();
    }

    /**
     * Add a new node
     */
    void addNode() {
        /* Add entry for node in adjList */
        adjList.add(new ArrayList<>());
        ++numNodes;
        /* New node is not offline */
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
            update(null);
        } else {
            /* Merge all adj edges */
            for (SimpleEdge edge: adjList.get(ID)) {
                if (!edge.isOffline) {
                    DSU.unionSet(ID, edge.destination);
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
        /* Todo: Intelligent initialization (See: AntNet 1.1) */
        /* Todo: Coefficient of memory (See: AntNet 1.1) */
        SimpleEdge forward = new SimpleEdge(node1, node2, cost);
        SimpleEdge backward = new SimpleEdge(node2, node1, cost);
        edgeList.add(forward);
        edgeList.add(backward);
        adjList.get(node1).add(forward);
        adjList.get(node2).add(backward);
        DSU.unionSet(node1, node2);
        update(node2); // OR: node1
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
            update(null);
        } else {
            DSU.unionSet(edge.source, edge.destination);
            update(edge.destination); // OR edge.source
        }
    }

    /**
     * Update DSU and heuristic
     *
     * @param node Updated node, or null if full rebuild
     */
    private void update(Integer node) {
        if (node == null) {
            initDSU();
            node = nodeID;
        }
        /* Todo: Rewrite this part */
        ArrayList<SimpleEdge> neighbours = new ArrayList<>();
        ArrayList<Integer> destinations = new ArrayList<>();
        for (SimpleEdge edge: adjList.get(nodeID)) { // Affected neighbours
            if (edge.isOffline || nodes.get(edge.destination)) continue;
            if (DSU.sameSet(edge.destination, node)) {
                neighbours.add(edge);
            }
        }
        for (int a = 0; a < numNodes; ++a) { // Affected destinations
            if (a == nodeID) continue;
            if (nodes.get(a)) continue;
            if (DSU.sameSet(a, node)) {
                destinations.add(a);
            }
        }
        for (SimpleEdge edge: neighbours) {
            for (Integer dest: destinations) {
                Double prev = pheromone.get(dest, edge.destination);
                if (prev == null) { // Previously not viable
                    if (DSU.sameSet(edge.destination, dest)) // Now viable
                        addHeuristic(edge.destination, dest);
                } else { // Previously viable
                    if (!DSU.sameSet(edge.destination, dest)) // Now not viable
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
        /* Change Value */
        Double old = pheromone.get(destination, neighbour);
        if (old + change < 0 || old + change > 1) throw new IllegalArgumentException();
        if (Math.abs(old + change) < EPS) {
            pheromone.put(destination, neighbour, null);
        } else {
            pheromone.put(destination, neighbour, old + change);
        }
        double tot = 1 - old;
        /* Decrease proportionally */
        for (int a = 0; a < numNodes; ++a) {
            if (a == neighbour) continue;
            Double val = pheromone.get(destination, a);
            if (val != null) {
                pheromone.put(destination, a, val * (1 + change / tot));
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
    private void addHeuristic(int neighbour, int destination) {
        int cnt = 0;
        for (SimpleEdge edge: adjList.get(nodeID)) {
            if (DSU.sameSet(edge.destination, destination)) ++cnt;
        }
        updateHeuristic(neighbour, destination, 1. / cnt);
    }

    /**
     * Remove a neighbour from consideration
     * as it is not viable now
     *
     * @param neighbour Node ID
     * @param destination Destination ID
     */
    private void removeHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, -pheromone.get(destination, neighbour));
    }
}

package com.ds2016;

import javafx.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {

    private final static double EPS = 1e-9, EXP = 1.4;

    final int speed, nodeID;
    final HashMap2D<Integer, Integer, Double> pheromone = new HashMap2D<>(); // Destination, Node
    final ArrayDeque<Ant> fastQ = new ArrayDeque<>();
    final ArrayDeque<Packet> slowQ = new ArrayDeque<>();
    private final double alpha;
    private final ArrayList<Node_EACO> nodes;
    private final ArrayList<Edge_ACO> edgeList;
    private final HashMap2D<Integer, Integer, Edge_ACO> adjMat;
    private final HashMap2D<Integer, Integer, Double> routing = new HashMap2D<>();
    boolean isOffline;
    private ArrayList<Integer> numViableNeighbours = new ArrayList<>();
    private UFDS DSU;

    /**
     * Initialize a node
     *
     * @param _speed    Processing speed
     * @param _nodes    ArrayList of Node_EACO
     * @param _edgeList ArrayList of Edge_ACO
     * @param _adjMat   Adjacency Matrix
     * @param _alpha    Weightage of pheromone
     */
    Node_EACO(int _speed, ArrayList<Node_EACO> _nodes, ArrayList<Edge_ACO> _edgeList,
              HashMap2D<Integer, Integer, Edge_ACO> _adjMat, double _alpha) {
        speed = _speed;
        nodeID = _nodes.size();
        alpha = _alpha;
        nodes = _nodes;
        edgeList = _edgeList;
        adjMat = _adjMat;
    }

    /**
     * Build pheromone table
     */
    void init() {
        initDSU();
        for (int a = 0; a < nodes.size(); ++a) { // For each destination
            if (a == nodeID) continue;
            int numNeighbours = 0; // Number of viable neighbours
            for (Edge_ACO edge : adjMat.get(nodeID).values()) {
                if (edge.isOffline || nodes.get(edge.destination).isOffline) continue;
                if (DSU.sameSet(edge.destination, a)) ++numNeighbours;
            }
            for (Edge_ACO edge : adjMat.get(nodeID).values()) { // For each neighbour
                if (edge.isOffline || nodes.get(edge.destination).isOffline) continue;
                if (!DSU.sameSet(edge.destination, a)) continue;
                pheromone.put(a, edge.destination, 1. / numNeighbours);
                routing.put(a, edge.destination, 1. / numNeighbours);
            }
            numViableNeighbours.add(numNeighbours);
        }
    }

    /**
     * Initialize the UFDS structure
     */
    private void initDSU() {
        DSU = new UFDS(nodes.size());
        for (SimpleEdge edge : edgeList) {
            if (edge.source == nodeID || edge.destination == nodeID) continue;
            if (edge.isOffline) continue;
            DSU.unionSet(edge.source, edge.destination);
        }
    }

    /**
     * React to toggling of node
     *
     * @param ID Node ID
     */
    void toggleNode(int ID) {
        if (nodes.get(nodeID).isOffline) {
            initDSU();
        } else {
            // Merge edges
            for (Edge_ACO edge : adjMat.get(ID).values()) {
                if (!edge.isOffline) {
                    // Don't merge self
                    if (edge.source == nodeID || edge.destination == nodeID) continue;
                    DSU.unionSet(edge.source, edge.destination);
                }
            }
        }
        update();
    }

    /**
     * React to addition of an edge
     *
     * @param node1 First node
     * @param node2 Second node
     */
    void addEdge(int node1, int node2) {
        // Don't merge self
        if (node1 == nodeID || node2 == nodeID) return;
        DSU.unionSet(node1, node2);
        update();
    }

    /**
     * React to toggling of an edge
     *
     * @param ID Edge ID
     */
    void toggleEdge(int ID) {
        // If self is involved, it doesn't affect viability
        int src = edgeList.get(ID).source;
        int dst = edgeList.get(ID).destination;
        if (src == nodeID || dst == nodeID)
            return;
        if (edgeList.get(ID).isOffline) {
            initDSU();
        } else {
            DSU.unionSet(src, dst);
        }
        update();
    }

    /**
     * Use heuristics to calculate the
     * next best hop, for a given destination
     *
     * @param packet Packet being processed
     * @return Neighbour for next hop, or null if no candidates
     */
    Integer nextHop(Packet packet) {
        double RNG = Math.random(), totVal = 0;
        double beta = 1 - alpha;
        ArrayList<Pair<Integer, Double>> neighbours = new ArrayList<>(); // Neighbour, Heuristic
        for (Edge_ACO edge : adjMat.get(nodeID).values()) {
            if (edge.isOffline) continue; // Link is offline
            if (nodes.get(edge.destination).isOffline) continue; // Node is offline
            if (!packet.canVisit(edge.destination)) continue; // Cycle detection
            Double tau = routing.get(packet.destination, edge.destination); // Pheromone
            if (tau == null) continue; // Not viable
            Double eta = 1. / edge.cost; // 1 / Distance
            neighbours.add(new Pair<>(edge.destination, Math.pow(tau, alpha) * Math.pow(eta, beta)));
            totVal += neighbours.get(neighbours.size() - 1).getValue();
        }
        if (neighbours.isEmpty()) {
            return null;
        }
        for (Pair<Integer, Double> neighbour : neighbours) {
            RNG -= neighbour.getValue() / totVal;
            if (RNG <= EPS) return neighbour.getKey();
        }
        return neighbours.get(neighbours.size() - 1).getKey();
    }

    /**
     * Update heuristic
     */
    private void update() {
        // Resize just in case
        while (numViableNeighbours.size() < nodes.size()) {
            numViableNeighbours.add(0);
        }
        ArrayList<Integer> neighbours = new ArrayList<>();
        ArrayList<Integer> destinations = new ArrayList<>();
        for (Edge_ACO edge : adjMat.get(nodeID).values()) {
            neighbours.add(edge.destination);
        }
        for (int a = 0; a < nodes.size(); ++a) {
            if (a == nodeID) continue;
            destinations.add(a);
        }
        for (Integer neighbour : neighbours) {
            for (Integer dest : destinations) {
                Double prev = pheromone.get(dest, neighbour);
                if (prev == null) { // Previously not viable
                    if (DSU.sameSet(neighbour, dest) &&
                            !adjMat.get(nodeID, neighbour).isOffline &&
                            !nodes.get(neighbour).isOffline) { // Now viable
                        numViableNeighbours.set(dest, numViableNeighbours.get(dest) + 1);
                        addHeuristic(neighbour, dest);
                    }
                } else { // Previously viable
                    if (!DSU.sameSet(neighbour, dest) ||
                            adjMat.get(nodeID, neighbour).isOffline ||
                            nodes.get(neighbour).isOffline) { // Now not viable
                        numViableNeighbours.set(dest, numViableNeighbours.get(dest) - 1);
                        removeHeuristic(neighbour, dest);
                    }
                }
            }
        }
    }

    /**
     * Change the value by a certain amount
     * Modifies other values proportionally to
     * achieve a sum of one
     *
     * @param neighbour   ID of neighbour
     * @param destination ID of destination
     * @param change      Pheromone change
     * @throws IllegalArgumentException if pheromone lies out of range [0, 1]
     */
    void updateHeuristic(int neighbour, int destination, double change) throws IllegalArgumentException {
        Double old = pheromone.get(destination, neighbour);
        double tot = 0;
        for (int a = 0; a < nodes.size(); ++a) {
            Double val = pheromone.get(destination, a);
            if (val != null) {
                tot += val;
            }
        }
        if (old == null) old = .0;
        if (Math.abs(old + change) < EPS) {
            pheromone.put(destination, neighbour, null);
        } else {
            if ((old + change) < -EPS || (old + change - 1) > EPS) throw new IllegalArgumentException();
            pheromone.put(destination, neighbour, old + change);
        }
        change += (tot - 1);
        tot -= old;
        /* Decrease proportionally */
        for (int a = 0; a < nodes.size(); ++a) {
            if (a == neighbour) continue;
            Double val = pheromone.get(destination, a);
            if (val != null) {
                pheromone.put(destination, a, val * (1 - change / tot));
            }
        }
        updateRouting(destination);
    }

    /**
     * Updating routing tables
     *
     * @param destination ID of destination
     */
    private void updateRouting(int destination) {
        double tot = 0;
        for (int a = 0; a < nodes.size(); ++a) {
            Double val = pheromone.get(destination, a);
            if (val != null) {
                tot += Math.pow(val, EXP);
                routing.put(destination, a, Math.pow(val, EXP));
            }
        }
        for (int a = 0; a < nodes.size(); ++a) {
            if (pheromone.get(destination, a) != null) {
                routing.put(destination, a, routing.get(destination, a) / tot);
            }
        }
    }

    /**
     * Add a neighbour to consideration
     * as it is viable now
     *
     * @param neighbour   Node ID
     * @param destination Destination ID
     */
    private void addHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, 1. / numViableNeighbours.get(destination));
    }

    /**
     * Remove a neighbour from consideration
     * as it is not viable now
     *
     * @param neighbour   Node ID
     * @param destination Destination ID
     */
    private void removeHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, -pheromone.get(destination, neighbour));
    }
}

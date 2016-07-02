package com.ds2016;

import javafx.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {

    private final static double EPS = 1e-5;

    final int speed, nodeID;
    final HashMap2D<Integer, Integer, Double> pheromone = new HashMap2D<>(); // Destination, Node
    final ArrayDeque<Ant> fastQ = new ArrayDeque<>();
    final ArrayDeque<Packet> slowQ = new ArrayDeque<>();
    private final double alpha;
    private final ArrayList<Node_EACO> nodes;
    private final ArrayList<Edge_ACO> edgeList;
    private final HashMap2D<Integer, Integer, Edge_ACO> adjMat;
    boolean isOffline;
    private UFDS DSU;

    /**
     * Initialize a node
     *
     * @param _speed Processing speed
     * @param _nodes ArrayList of Node_EACO
     * @param _edgeList ArrayList of Edge_ACO
     * @param _adjMat Adjacency Matrix
     * @param _alpha Weightage of pheromone
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
            for (Edge_ACO edge: adjMat.get(nodeID).values()) {
                if (edge.isOffline || nodes.get(edge.destination).isOffline) continue;
                if (DSU.sameSet(edge.destination, a)) ++numNeighbours;
            }
            for (Edge_ACO edge: adjMat.get(nodeID).values()) { // For each neighbour
                pheromone.put(a, edge.destination, 1. / numNeighbours);
            }
        }
    }

    /**
     * Initialize the UFDS structure
     */
    private void initDSU() {
        DSU = new UFDS(nodes.size());
        for (SimpleEdge edge: edgeList) {
            if (edge.source == nodeID || edge.destination == nodeID) continue;
            if (edge.isOffline) continue;
            DSU.unionSet(edge.source, edge.destination);
        }
    }

    /**
     * Use heuristics to calculate the
     * next best hop, for a given destination
     *
     * @param packet Packet being processed
     * @return Neighbour for next hop, or null if no candidates
     */
    Integer nextHop(Packet packet) {
        double RNG = Math.random(), totVal = .0;
        double beta = 1 - alpha;
        ArrayList<Pair<Integer, Double>> neighbours = new ArrayList<>(); // Neighbour, Heuristic
        for (Edge_ACO edge: adjMat.get(nodeID).values()) {
            if (edge.isOffline) continue; // Link is offline
            if (nodes.get(edge.destination).isOffline) continue; // Node is offline
            if (!packet.canVisit(edge.destination)) continue; // Cycle detection
            Double tau = pheromone.get(packet.destination, edge.destination); // Pheromone
            if (tau == null) continue; // Not viable
            Double eta = 1. / edge.cost; // 1 / Distance
            neighbours.add(new Pair<>(edge.destination, Math.pow(tau, alpha) * Math.pow(eta, beta)));
            totVal += neighbours.get(neighbours.size() - 1).getValue();
        }
        if (neighbours.isEmpty()) {
            return null;
        }
        for (Pair<Integer, Double> neighbour: neighbours) {
            RNG -= neighbour.getValue() / totVal;
            if (RNG <= EPS) return neighbour.getKey();
        }
        return neighbours.get(neighbours.size() - 1).getKey();
    }

    /**
     * Rebuild DSU
     * Update heuristic
     */
    void rebuild() {
        ArrayList<Integer> neighbours = new ArrayList<>();
        ArrayList<Integer> destinations = new ArrayList<>();
        initDSU();
        for (Edge_ACO edge: adjMat.get(nodeID).values()) {
            neighbours.add(edge.destination);
        }
        for (int a = 0; a < nodes.size(); ++a) {
            if (a == nodeID) continue;
            destinations.add(a);
        }
        for (Integer neighbour: neighbours) {
            for (Integer dest: destinations) {
                Double prev = pheromone.get(dest, neighbour);
                if (prev == null) { // Previously not viable
                    if (DSU.sameSet(neighbour, dest) &&
                            !adjMat.get(nodeID, neighbour).isOffline &&
                            !nodes.get(neighbour).isOffline) // Now viable
                        addHeuristic(neighbour, dest);
                } else { // Previously viable
                    if (!DSU.sameSet(neighbour, dest) ||
                            adjMat.get(nodeID, neighbour).isOffline ||
                            nodes.get(neighbour).isOffline) // Now not viable
                        removeHeuristic(neighbour, dest);
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
     * @throws IllegalArgumentException
     */
    void updateHeuristic(int neighbour, int destination, double change) throws IllegalArgumentException {
        Double old = pheromone.get(destination, neighbour);
        if (old == null) {
            old = .0;
            pheromone.put(destination, neighbour, change);
        } else if (Math.abs(old + change) < EPS) {
            pheromone.put(destination, neighbour, null);
        } else {
            if (old + change < 0 || old + change > 1) throw new IllegalArgumentException();
            pheromone.put(destination, neighbour, old + change);
        }
        double tot = 1 - old;
        /* Decrease proportionally */
        for (int a = 0; a < nodes.size(); ++a) {
            if (a == neighbour) continue;
            Double val = pheromone.get(destination, a);
            if (val != null) {
                pheromone.put(destination, a, val * (1 - change / tot));
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
        for (Edge_ACO edge: adjMat.get(nodeID).values()) {
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

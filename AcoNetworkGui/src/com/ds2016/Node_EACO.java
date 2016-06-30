package com.ds2016;

import javafx.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO implements Node_ACO {

    final int speed, nodeID;
    final HashMap2D<Integer, Integer, Double> pheromone = new HashMap2D<>(); // Destination, Node
    final Queue<Ant> fastQ = new ArrayDeque<>();
    final Queue<Packet> slowQ = new ArrayDeque<>();
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
     * Initialize the UFDS structure
     */
    private void initDSU() {
        DSU = new UFDS(nodes.size());
        for (SimpleEdge edge: edgeList) {
            if (edge.source == nodeID || edge.destination == nodeID) continue;
            DSU.unionSet(edge.source, edge.destination);
        }
    }

    /**
     * Use heuristics to calculate the
     * next best hop, for a given destination
     *
     * @param ant Ant being processed
     * @return Neighbour for next hop, or null if no candidates
     */
    public Integer antNextHop(Ant ant) {
        double RNG = Math.random(), totVal = .0;
        double beta = 1 - alpha;
        ArrayList<Pair<Integer, Double>> neighbours = new ArrayList<>(); // Neighbour, Heuristic
        for (Edge_ACO edge: adjMat.get(ant.source).values()) {
            if (edge.isOffline) continue; // Link is offline
            if (nodes.get(edge.destination).isOffline) continue; // Node is offline
            if (!ant.canVisit(edge.destination)) continue; // Cycle detection
            Double tau = pheromone.get(ant.destination, edge.destination); // Pheromone
            if (tau == null) continue; // Not viable
            Double eta = 1. / edge.cost; // 1 / Distance
            neighbours.add(new Pair<>(edge.destination, Math.pow(tau, alpha) * Math.pow(eta, beta)));
            totVal += neighbours.get(neighbours.size() - 1).getValue();
        }
        if (neighbours.isEmpty()) {
            int cycleSize = ant.getCycleSize(adjMat.get(ant.source).values());
            if (cycleSize * 2 <= ant.path.size()) {
                return -ant.deleteCycle(adjMat.get(ant.source).values()); // -ve to indicate a cycle
            } else {
                return null; // Just give up
            }
        }
        for (Pair<Integer, Double> neighbour: neighbours) {
            RNG -= neighbour.getValue() / totVal;
            if (RNG <= EPS) return neighbour.getKey();
        }
        return neighbours.get(neighbours.size() - 1).getKey();
    }

    /**
     * Use heuristics to calculate the
     * next best hop, for a given destination
     *
     * @param packet Packet being processed
     * @return Neighbour for next hop
     */
    public int packetNextHop(Packet packet) {
        double RNG = Math.random(), totVal = .0;
        double beta = 1 - alpha;
        ArrayList<Pair<Integer, Double>> neighbours = new ArrayList<>(); // Neighbour, Heuristic
        for (Edge_ACO edge: adjMat.get(packet.source).values()) {
            if (edge.isOffline) continue; // Link is offline
            if (nodes.get(edge.destination).isOffline) continue; // Node is offline
            Double tau = pheromone.get(packet.destination, edge.destination); // Pheromone
            if (tau == null) continue; // Not viable
            Double eta = 1. / edge.cost; // 1 / Distance
            neighbours.add(new Pair<>(edge.destination, Math.pow(tau, alpha) * Math.pow(eta, beta)));
            totVal += neighbours.get(neighbours.size() - 1).getValue();
        }
        for (Pair<Integer, Double> neighbour: neighbours) {
            RNG -= neighbour.getValue() / totVal;
            if (RNG <= EPS) return neighbour.getKey();
        }
        return neighbours.get(neighbours.size() - 1).getKey();
    }

    /**
     * React to toggling of a node
     *
     * @param ID Node ID
     */
    public void toggleNode(int ID) {
        if (nodes.get(ID).isOffline) {
            update(null);
        } else {
            for (Edge_ACO edge: adjMat.get(ID).values()) {
                if (!edge.isOffline) {
                    DSU.unionSet(ID, edge.destination);
                }
            }
            update(ID);
        }
    }

    /**
     * React to addition of an edge
     *
     * @param node1 First Node
     * @param node2 Second Node
     */
    public void addEdge(int node1, int node2) {
        /* Todo: Intelligent initialization (See: AntNet 1.1) */
        /* Todo: Coefficient of memory (See: AntNet 1.1) */
        DSU.unionSet(node1, node2);
        update(node2); // OR: node1
    }

    /**
     * React to toggling of an edge
     *
     * @param ID Edge ID
     */
    public void toggleEdge(int ID) {
        Edge_ACO edge = edgeList.get(ID);
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
        ArrayList<Edge_ACO> neighbours = new ArrayList<>();
        ArrayList<Integer> destinations = new ArrayList<>();
        if (node == null) {
            initDSU();
            neighbours = new ArrayList<>(adjMat.get(nodeID).values());
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                destinations.add(a);
            }
        } else {
            for (Edge_ACO edge: adjMat.get(nodeID).values()) { // Affected neighbours
                if (DSU.sameSet(edge.destination, node)) {
                    neighbours.add(edge);
                }
            }
            for (int a = 0; a < nodes.size(); ++a) { // Affected destinations
                if (a == nodeID) continue;
                if (DSU.sameSet(a, node)) {
                    destinations.add(a);
                }
            }
        }
        for (Edge_ACO edge: neighbours) {
            for (Integer dest: destinations) {
                Double prev = pheromone.get(dest, edge.destination);
                if (prev == null) { // Previously not viable
                    if (DSU.sameSet(edge.destination, dest) &&
                            !edge.isOffline &&
                            !nodes.get(edge.destination).isOffline) // Now viable
                        addHeuristic(edge.destination, dest);
                } else { // Previously viable
                    if (!DSU.sameSet(edge.destination, dest) ||
                            edge.isOffline ||
                            nodes.get(edge.destination).isOffline) // Now not viable
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

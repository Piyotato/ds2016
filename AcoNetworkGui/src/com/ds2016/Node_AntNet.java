package com.ds2016;

import javafx.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by damian on 27/5/16.
 */
class Node_AntNet {

    private final static double EPS = 1e-9, EXP = 1.4;

    final int speed, nodeID;
    final HashMap2D<Integer, Integer, Double> pheromone = new HashMap2D<>(); // Destination, Node
    final ArrayDeque<Ant> fastQ = new ArrayDeque<>();
    final ArrayDeque<Packet> slowQ = new ArrayDeque<>();
    private final double alpha;
    private final ArrayList<Node_AntNet> nodes;
    private final HashMap2D<Integer, Integer, Edge_ACO> adjMat;
    private final HashMap2D<Integer, Integer, Double> routing = new HashMap2D<>();
    boolean isOffline;
    private int numNeighbours;

    /**
     * Initialize a node
     *
     * @param _speed  Processing speed
     * @param _nodes  ArrayList of Node_AntNet
     * @param _adjMat Adjacency Matrix
     * @param _alpha  Weightage of pheromone
     */
    Node_AntNet(int _speed, ArrayList<Node_AntNet> _nodes,
                HashMap2D<Integer, Integer, Edge_ACO> _adjMat, double _alpha) {
        speed = _speed;
        nodeID = _nodes.size();
        alpha = _alpha;
        nodes = _nodes;
        adjMat = _adjMat;
    }

    /**
     * Build pheromone table
     */
    void init() {
        for (Edge_ACO edge : adjMat.get(nodeID).values()) {
            if (!edge.isOffline && !nodes.get(edge.destination).isOffline)
                ++numNeighbours;
        }
        for (int a = 0; a < nodes.size(); ++a) { // For each destination
            if (a == nodeID) continue;
            for (Edge_ACO edge : adjMat.get(nodeID).values()) { // For each neighbour
                pheromone.put(a, edge.destination, 1. / numNeighbours);
                routing.put(a, edge.destination, 1. / numNeighbours);
            }
        }
    }

    /**
     * React to toggling of node
     *
     * @param ID Node ID
     */
    void toggleNode(int ID) {
        if (nodes.get(ID).isOffline) {
            --numNeighbours;
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                removeHeuristic(ID, a);
            }
        } else {
            ++numNeighbours;
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                addHeuristic(ID, a);
            }
        }
    }

    /**
     * React to addition of an edge
     *
     * @param node Other Node
     */
    void addEdge(int node) {
        ++numNeighbours;
        for (int a = 0; a < nodes.size(); ++a) {
            if (a == nodeID) continue;
            addHeuristic(node, a);
        }
    }

    /**
     * React to toggling of an edge
     *
     * @param node Other Node
     */
    void toggleEdge(int node) {
        if (adjMat.get(nodeID, node).isOffline) {
            --numNeighbours;
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                removeHeuristic(node, a);
            }
        } else {
            ++numNeighbours;
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                addHeuristic(node, a);
            }
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
        double RNG = Math.random(), totVal = 0;
        double beta = 1 - alpha;
        ArrayList<Pair<Integer, Double>> neighbours = new ArrayList<>(); // Neighbour, Heuristic
        for (Edge_ACO edge : adjMat.get(nodeID).values()) {
            if (edge.isOffline) continue; // Link is offline
            if (nodes.get(edge.destination).isOffline) continue; // Node is offline
            if (!packet.canVisit(edge.destination)) continue; // Cycle detection
            Double tau = routing.get(packet.destination, edge.destination); // Pheromone
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
     * Give pheromone to a new neighbour
     *
     * @param neighbour   Node ID
     * @param destination Destination ID
     */
    private void addHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, 1. / numNeighbours);
    }

    /**
     * Remove pheromone from a node that is
     * no longer a neighbour
     *
     * @param neighbour   Node ID
     * @param destination Destination ID
     */
    private void removeHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, -pheromone.get(destination, neighbour));
    }
}

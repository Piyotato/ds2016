package com.damianhxy;

import java.util.*;
import javafx.util.*;

/**
 * Created by damian on 27/5/16.
 */
public class Node_ACO extends Node {

    private final static double EPS = 1e-5;

    final HashMap2D<Integer, Integer, Double> pheromone = new HashMap2D<>(); // Destination, Node
    private final int alpha, beta, tabuSize;

    final Queue<Ant> fastQ = new ArrayDeque<>();
    final Queue<Packet> slowQ = new ArrayDeque<>();

    private final ArrayList<Node_EACO> nodes;
    private final ArrayList<Edge_ACO> edgeList;
    private final HashMap2D<Integer, Integer, Edge_ACO> adjMat;

    /**
     * Initialize a node
     *
     * @param _speed Processing speed
     * @param _nodes ArrayList of Node_EACO
     * @param _edgeList ArrayList of Edge_ACO
     * @param _adjMat Adjacency Matrix
     * @param _alpha Weightage of pheromone
     * @param _beta Weightage of cost function
     * @param _tabuSize Size of tabu list
     */
    Node_ACO(int _speed, ArrayList<Node_EACO> _nodes, ArrayList<Edge_ACO> _edgeList,
             HashMap2D<Integer, Integer, Edge_ACO> _adjMat, int _alpha, int _beta, int _tabuSize) {
        super(_speed, _nodes.size());
        alpha = _alpha;
        beta = _beta;
        tabuSize = _tabuSize;
        nodes = _nodes;
        edgeList = _edgeList;
        adjMat = _adjMat;
    }

    /**
     * React to toggling of node
     *
     * @param ID Node ID
     */
    void toggleNode(int ID) {
        /* Only care about neighbours */
        if (!adjMat.get(nodeID).keySet().contains(ID)) return;
        if (nodes.get(ID).isOffline) {
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                removeHeuristic(ID, a);
            }
        } else {
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                addHeuristic(ID, a);
            }
        }
    }

    /**
     * React to addition of an edge
     *
     * @param node1 First Node
     * @param node2 Second Node
     */
    void addEdge(int node1, int node2) {
        /* Only care about neighbours */
        if (node1 != nodeID && node2 != nodeID) return;
        int otherNode = (node1 == nodeID ? node2: node1);
        for (int a = 0; a < nodes.size(); ++a) {
            if (a == nodeID) continue;
            addHeuristic(otherNode, a);
        }
    }

    /**
     * React to toggling of an edge
     *
     * @param ID Edge ID
     */
    void toggleEdge(int ID) {
        /* Only care about neighbours */
        Edge_ACO edge = edgeList.get(ID);
        if (edge.source != nodeID && edge.destination != nodeID) return;
        int otherNode = (edge.source == nodeID ? edge.destination : edge.source);
        if (edge.isOffline) {
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                removeHeuristic(otherNode, a);
            }
        } else {
            for (int a = 0; a < nodes.size(); ++a) {
                if (a == nodeID) continue;
                addHeuristic(otherNode, a);
            }
        }
    }


    /**
     * Use heuristics to calculate the
     * next best hop, for a given destination
     *
     * @return Neighbour for next hop, or null if no candidates
     */
    Integer nextHop(Packet packet) {
        double RNG = Math.random(), totVal = .0;
        ArrayList<Pair<Integer, Double>> neighbours = new ArrayList<>(); // Neighbour, Heuristic
        for (Edge_ACO edge: adjMat.get(nodeID).values()) {
            if (edge.isOffline) continue;
            if (nodes.get(edge.destination).isOffline) continue;
            if (!packet.isValid(edge.destination, tabuSize)) continue; // Cycle detection
            Double tau = pheromone.get(packet.destination, edge.destination); // Pheromone
            if (tau == null) continue; // Not viable
            Double eta = 1. / edge.cost; // 1 / Distance
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
     * Change the value by a certain amount
     * Modifies other values proportionally to
     * achieve a sum of one
     *
     * @param neighbour ID of neighbour
     * @param destination ID of destination
     * @param change Pheromone change
     */
    void updateHeuristic(int neighbour, int destination, double change) throws IllegalArgumentException {
        Double old = pheromone.get(destination, neighbour);
        if (old == null) old = .0;
        if (old + change < 0 || old + change > 1) throw new IllegalArgumentException();
        pheromone.put(destination, neighbour, old + change);
        double tot = 1 - old;
        for (int a = 0; a < nodes.size(); ++a) {
            if (a == neighbour) continue;
            Double val = pheromone.get(destination, a);
            if (val != null) {
                pheromone.put(destination, a, val * (1 + change / tot));
            }
        }
    }

    /**
     * Give pheromone to a new neighbour
     *
     * @param neighbour Node ID
     * @param destination Destination ID
     */
    private void addHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, 1. / adjMat.get(nodeID).size());
    }

    /**
     * Remove pheromone from a node that is
     * no longer a neighbour
     *
     * @param neighbour Node ID
     * @param destination Destination ID
     */
    private void removeHeuristic(int neighbour, int destination) {
        updateHeuristic(neighbour, destination, -pheromone.get(destination, neighbour));
    }
}

package com.ds2016;

import javafx.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by damian on 16/5/16.
 */
class Node_EACO {

    private final static double EPS = 1e-9, EXP = 1.4;
    private final static int PRECISION = 10;

    final int ID;
    final HashMap<Integer, ArrayDeque<Ant>> fastQ = new HashMap<>();
    final HashMap<Integer, ArrayDeque<Packet>> slowQ = new HashMap<>();
    private final double alpha;
    private final ArrayList<Node_EACO> nodes;
    private final ArrayList<Edge_ACO> edgeList;
    private final ArrayList<Integer> numViableNeighbours = new ArrayList<>();
    private final HashMap2D<Integer, Integer, Edge_ACO> adjMat;
    private final HashMap2D<Integer, Integer, Double> pheromone = new HashMap2D<>(); // Destination, Node
    private final HashMap2D<Integer, Integer, Double> routing = new HashMap2D<>();
    boolean isOffline;
    private UFDS DSU;

    /**
     * Initialize a node
     *
     * @param _nodes    ArrayList of Node_EACO
     * @param _edgeList ArrayList of Edge_ACO
     * @param _adjMat   Adjacency Matrix
     * @param _alpha    Weightage of pheromone
     */
    Node_EACO(ArrayList<Node_EACO> _nodes, ArrayList<Edge_ACO> _edgeList,
              HashMap2D<Integer, Integer, Edge_ACO> _adjMat, double _alpha) {
        ID = _nodes.size();
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
            numViableNeighbours.add(0);
            if (a == ID) continue;
            int numNeighbours = 0; // Number of viable neighbours
            for (Edge_ACO edge : adjMat.get(ID).values()) {
                if (edge.isOffline || nodes.get(edge.destination).isOffline) continue;
                if (DSU.sameSet(edge.destination, a)) ++numNeighbours;
            }
            numViableNeighbours.set(a, numNeighbours);
            for (Edge_ACO edge : adjMat.get(ID).values()) { // For each neighbour
                if (edge.isOffline || nodes.get(edge.destination).isOffline) continue;
                if (!DSU.sameSet(edge.destination, a)) continue;
                addHeuristic(edge.destination, a);
            }
        }
    }

    /**
     * Initialize the UFDS structure
     */
    private void initDSU() {
        DSU = new UFDS(nodes.size());
        for (SimpleEdge edge : edgeList) {
            if (edge.source == ID || edge.destination == ID) continue;
            if (nodes.get(edge.source).isOffline || nodes.get(edge.destination).isOffline) continue;
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
        if (nodes.get(ID).isOffline) {
            initDSU();
        } else {
            // Merge edges
            for (Edge_ACO edge : adjMat.get(ID).values()) {
                if (edge.source == ID || edge.destination == ID) continue;
                if (nodes.get(edge.destination).isOffline) continue; // edge.source not offline
                if (edge.isOffline) continue;
                DSU.unionSet(edge.source, edge.destination);
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
        if (node1 == ID || node2 == ID) return;
        if (nodes.get(node1).isOffline || nodes.get(node2).isOffline) return;
        // edge not offline
        DSU.unionSet(node1, node2);
        update();
    }

    /**
     * React to toggling of an edge
     *
     * @param ID Edge ID
     */
    void toggleEdge(int ID) {
        int src = edgeList.get(ID).source;
        int dst = edgeList.get(ID).destination;
        if (src == ID || dst == ID) return;
        if (edgeList.get(ID).isOffline) {
            initDSU();
        } else {
            if (nodes.get(src).isOffline || nodes.get(dst).isOffline) return;
            // edge not offline
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
    private Integer nextHop(Packet packet) {
        double RNG = Math.random(), totVal = 0;
        double beta = 1 - alpha;
        ArrayList<Pair<Integer, Double>> neighbours = new ArrayList<>(); // Neighbour, Heuristic
        for (Edge_ACO edge : adjMat.get(ID).values()) {
            if (edge.isOffline) continue; // Link is offline
            if (nodes.get(edge.destination).isOffline) continue; // Node is offline
            if (packet instanceof Ant && !((Ant) packet).canVisit(edge.destination)) continue; // Cycle detection
            Double tau = routing.get(packet.destination, edge.destination); // Pheromone
            if (tau == null) continue; // Not viable
            double eta = 1. / edge.cost; // 1 / Distance
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
        for (Edge_ACO edge : adjMat.get(ID).values()) {
            for (int dest = 0; dest < nodes.size(); ++dest) {
                if (dest == ID) continue;
                int neighbour = edge.destination;
                Double prev = pheromone.get(dest, neighbour);
                if (prev == null) { // Previously not viable
                    if (DSU.sameSet(neighbour, dest) &&
                            !adjMat.get(ID, neighbour).isOffline &&
                            !nodes.get(neighbour).isOffline) { // Now viable
                        numViableNeighbours.set(dest, numViableNeighbours.get(dest) + 1);
                        addHeuristic(neighbour, dest);
                    }
                } else { // Previously viable
                    if (!DSU.sameSet(neighbour, dest) ||
                            adjMat.get(ID, neighbour).isOffline ||
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
    private void updateHeuristic(int neighbour, int destination, double change) throws IllegalArgumentException {
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
     * Process an ant
     *
     * @param ant Ant
     */
    void processAnt(Ant ant) {
        Integer nxt;
        if (ant.isBackwards) { // Backward ant
            ant.updateTotalTime();
            int prev = ant.previousNode();
            Double P = pheromone.get(ant.destination, prev);
            if (P == null) {
                return; // Previous Node is gone
            }
            double R = 1. / (ant.totalTime * PRECISION);
            double change = (P * (1 - R) + R) - P;
            updateHeuristic(prev, ant.destination, change);
            if (ant.source == ID) {
                return; // Reached source
            }
            nxt = ant.nextNode();
            if (pheromone.get(ant.destination, prev) == null) {
                return; // Next node is gone
            }
            fastQ.putIfAbsent(nxt, new ArrayDeque<>());
            fastQ.get(nxt).push(ant);
        } else { // Forward ant
            ant.addNode(ID);
            if (ant.destination == ID) {
                ant.isBackwards = true;
                nxt = ant.nextNode();
                if (pheromone.get(ant.destination, nxt) == null) {
                    return; // Next node is gone
                }
                fastQ.putIfAbsent(nxt, new ArrayDeque<>());
                ant.timings.add(getDepletionTime(nxt));
                fastQ.get(nxt).push(ant);
            } else {
                nxt = nextHop(ant);
                if (nxt == null) {
                    return; // No valid next node
                }
                fastQ.putIfAbsent(nxt, new ArrayDeque<>());
                ant.timings.add(getDepletionTime(nxt));
                ant.timings.add((double) adjMat.get(ID, nxt).cost);
                fastQ.get(nxt).push(ant);
            }
        }
    }

    /**
     * Process a packet
     *
     * @param packet Packet
     * @return 1, if packet has reached destination
     */
    int processPacket(Packet packet) {
        if (packet.destination == ID) {
            return 1;
        }
        Integer nxt = nextHop(packet);
        if (nxt != null) {
            slowQ.putIfAbsent(nxt, new ArrayDeque<>());
            slowQ.get(nxt).push(packet);
        }
        return 0;
    }

    /**
     * Clear the ant queue
     */
    void clearFastQ() {
        fastQ.clear();
    }

    /**
     * Clear the packet queue
     */
    void clearSlowQ() {
        slowQ.clear();
    }

    /**
     * Toggle isOffline
     */
    void toggle() {
        isOffline ^= true;
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
        boolean destinationIsNeighbour = false;
        for (Edge_ACO edge : adjMat.get(ID).values()) {
            if (edge.isOffline || nodes.get(edge.destination).isOffline) continue;
            if (edge.destination == destination) destinationIsNeighbour = true;
        }
        double NN = numViableNeighbours.get(destination);
        if (destinationIsNeighbour) {
            // Intelligent Initialization
            if (neighbour == destination) {
                double amt = 1. / NN + 3. / 2. * (NN - 1) / (NN * NN);
                updateHeuristic(neighbour, destination, amt);
            } else {
                double amt = 1. / NN - 3. / 2. * 1. / (NN * NN);
                updateHeuristic(neighbour, destination, amt);
            }
        } else {
            updateHeuristic(neighbour, destination, 1. / NN);
        }
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

    /**
     * Time needed to deplete queue
     *
     * @param neighbour Neighbour ID
     */
    private double getDepletionTime(int neighbour) {
        return (double) fastQ.get(neighbour).size() / adjMat.get(ID, neighbour).bandwidth;
    }
}

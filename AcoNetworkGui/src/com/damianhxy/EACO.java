package com.damianhxy;

import java.util.*;
import javafx.util.*;

/**
 * Created by damian on 16/5/16.
 */
public class EACO extends AlgorithmBase {

    private int success, failure;
    private final int alpha, beta, ratio, tabuSize, TTL;
    private final ArrayList<Node_EACO> nodes = new ArrayList<>();
    private final ArrayList<Edge_ACO> edgeList = new ArrayList<>();
    private final ArrayList<ArrayList<Edge_ACO>> adjList = new ArrayList<>();

    /**
     * Initialize EACO
     *
     * @param _alpha Weightage of pheromone
     * @param _beta Weightage of cost function
     * @param _ratio Ratio of ants to packets
     * @param _TTL Time To Live of packets
     * @param _source Source node
     * @param _destination Destination node
     */
    public EACO(int _alpha, int _beta, int _ratio, int _TTL, int _tabuSize, int _source, int _destination) {
        super(_source, _destination);
        alpha = _alpha;
        beta = _beta;
        ratio = _ratio;
        TTL = _TTL;
        tabuSize = _tabuSize;
    }

    /**
     * Add a new node
     *
     * @param speed Processing speed
     */
    void addNode(int speed) {
        /* Propagated update */
        nodes.forEach(Node_EACO::addNode);
        /* For simulation purposes */
        nodes.add(new Node_EACO(numNodes++, speed, edgeList));
        adjList.add(new ArrayList<>());
    }

    /**
     * Toggle state of a node
     * Toggles states of neighbouring edges
     *
     * @param ID Node ID
     */
    void toggleNode(int ID) throws IllegalArgumentException {
        if (ID == source || ID == destination) {
            throw new IllegalArgumentException();
        }
        /* Propagated update */
        for (Node_EACO node: nodes) {
            node.toggleNode(ID);
        }
        /* For simulation purposes */
        Node_EACO node = nodes.get(ID);
        node.isOffline ^= true;
        if (node.isOffline) {
            node.fastQ.clear();
            node.slowQ.clear();
        }
    }

    /**
     * Add a bidirectional edge
     *
     * @param node1 First node
     * @param node2 Second node
     * @param cost Time taken
     */
    void addEdge(int node1, int node2, int cost) throws IllegalArgumentException {
        if (node1 >= numNodes || node2 >= numNodes) {
            throw new IllegalArgumentException();
        }
        /* Propagated update */
        for (Node_EACO node: nodes) {
            node.addEdge(node1, node2, cost);
        }
        /* For simulation purposes */
        Edge_ACO forward = new Edge_ACO(node1, node2, cost);
        Edge_ACO backward = new Edge_ACO(node2, node1, cost);
        edgeList.add(forward);
        edgeList.add(backward);
        adjList.get(node1).add(forward);
        adjList.get(node2).add(backward);
    }

    /**
     * Toggle state of an edge
     *
     * @param ID Edge ID
     */
    void toggleEdge(int ID) {
        /* Propagated update */
        for (Node_EACO node: nodes) {
            node.toggleEdge(ID * 2);
            node.toggleEdge(ID * 2 + 1);
        }
        /* For simulation purposes */
        Edge_ACO forward = edgeList.get(ID * 2);
        forward.isOffline ^= true;
        if (forward.isOffline) {
            failure += forward.packets.size() + forward.ants.size();
            forward.packets.clear();
            forward.ants.clear();
        }
        Edge_ACO backward = edgeList.get(ID * 2 + 1);
        backward.isOffline ^= true;
        if (backward.isOffline) {
            failure += backward.packets.size() + backward.ants.size();
            backward.packets.clear();
            backward.ants.clear();
        }
    }

    /**
     * Simulate node
     *
     * @param node Node being processed
     */
    private void processNode(Node_EACO node) throws IllegalStateException {
        int left = node.speed;
        while (!node.fastQ.isEmpty() && left-- > 0) {
            Ant ant = node.fastQ.poll();
            if (ant.isBackwards) { // Backward ant
                if (node.nodeID != ant.destination) {
                    int prev = ant.previousNode();
                    node.updateHeuristic(prev, ant.destination, 1. / ant.totalTime);
                }
                if (ant.source == node.nodeID) continue; // Reached source
                int nxt = ant.nextNode();
                ant.nextHop = nxt;
                if (nodes.get(nxt).isOffline) continue; // Drop Ant
                boolean found = false;
                for (Edge_ACO edge: adjList.get(node.nodeID)) {
                    if (edge.destination == nxt) {
                        edge.addAnt(ant, currentTime);
                        found = true;
                        break;
                    }
                }
                if (!found) throw new IllegalStateException();
            } else { // Forward ant
                Integer nxt = node.nextHop(ant, alpha, beta, tabuSize);
                if (nxt == null) continue; // Drop Ant
                ant.nextHop = nxt;
                ant.addNode(nxt);
                ant.totalTime += (double)node.slowQ.size() / node.speed;
                boolean found = false;
                for (Edge_ACO edge: adjList.get(node.nodeID)) {
                    if (edge.destination == nxt) {
                        edge.addAnt(ant, currentTime);
                        found = true;
                        break;
                    }
                }
                if (!found) throw new IllegalStateException();
            }
        }
        while (!node.slowQ.isEmpty() && left-- > 0) {
            Packet packet = node.slowQ.poll();
            Integer nxt = node.nextHop(packet, alpha, beta, tabuSize);
            if (nxt == null) {
                ++failure;
                continue; // Drop packet
            }
            packet.nextHop = nxt;
            packet.addNode(nxt);
            if (packet.destination == node.nodeID) {
                ++success;
                continue;
            }
            boolean found = false;
            for (Edge_ACO edge: adjList.get(node.nodeID)) {
                if (edge.destination == nxt) {
                    edge.addPacket(packet, currentTime);
                    found = true;
                    break;
                }
            }
            if (!found) throw new IllegalStateException();
        }
    }

    /**
     * Simulate edge
     *
     * @param edge Edge being processed
     */
    private void processEdge(Edge_ACO edge) {
        int left = edge.cost;
        while (!edge.ants.isEmpty() && left > 0) {
            if (edge.ants.peek().timestamp > currentTime) break;
            Ant ant = edge.ants.poll();
            --left;
            if (nodes.get(ant.nextHop).isOffline) continue; // Drop this Ant
            if (ant.nextHop == destination || ant.decrementTTL()) {
                nodes.get(ant.nextHop).fastQ.add(ant);
            }
        }
        while (!edge.packets.isEmpty() && left > 0) {
            if (edge.packets.peek().timestamp > currentTime) break;
            Packet packet = edge.packets.poll();
            --left;
            if (nodes.get(packet.nextHop).isOffline) {
                ++failure;
                continue; // Drop this packet
            }
            if (packet.nextHop == destination || packet.decrementTTL()) {
                nodes.get(packet.nextHop).slowQ.add(packet);
            } else {
                ++failure;
            }
        }
    }

    private void generatePackets() {
        Node_EACO src = nodes.get(source);
        int amt = src.speed;
        int numPackets = ratio * amt / (ratio + 1);
        int numAnts = amt / ratio;
        for (int a = 0; a < numPackets; ++a)
            src.slowQ.add(new Packet(source, destination, TTL));
        for (int a = 0; a < numAnts; ++a)
            src.fastQ.add(new Ant(source, destination, TTL));
    }

    /**
     * One tick of the simulation
     *
     * @return Success, Failure
     */
    Pair<Integer, Integer> tick() {
        ++currentTime;
        generatePackets();
        for (Edge_ACO edge: edgeList) {
            if (edge.isOffline) continue;
            processEdge(edge);
        }
        for (Node_EACO node: nodes) {
            if (node.isOffline) continue;
            processNode(node);
        }
        Pair<Integer, Integer> ret = new Pair<>(success, failure);
        success = failure = 0;
        return ret;
    }
}

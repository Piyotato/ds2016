package com.damianhxy;

import java.util.*;
import javafx.util.*;

/**
 * Created by damian on 16/5/16.
 */
public class EACO extends AlgoBase {

    private int success, failure;
    private int alpha, beta, ratio, tabuSize, TTL;
    private ArrayList<Node_EACO> nodes;
    private ArrayList<Edge_ACO> edgeList;
    private ArrayList<ArrayList<Edge_ACO>> adjList;

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
     */
    void addNode() {
        /* Propagated update */
        for (Node_EACO node: nodes) {
            node.addNode();
        }
        /* For simulation purposes */
        nodes.add(new Node_EACO(numNodes++, edgeList));
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
        /* Todo: Perhaps ensure that the graph is always complete */
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
        /* Todo: Perhaps ensure that there are no multi-edges */
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
            node.toggleEdge(ID);
        }
        /* For simulation purposes */
        Edge_ACO edge = edgeList.get(ID);
        edge.isOffline ^= true;
        if (edge.isOffline) {
            failure += edge.packets.size() + edge.ants.size();
            edge.packets.clear();
            edge.ants.clear();
        }

    }

    /**
     * Simulate node
     *
     * @param node Node being processed
     */
    private void processNode(Node_EACO node) throws IllegalStateException {
        int left = node.speed * SIM_SPEED;
        while (!node.fastQ.isEmpty() && left-- > 0) {
            Ant ant = node.fastQ.poll();
            if (ant.isBackwards) { // Backward ant
                if (node.NODEID != ant.destination) {
                    int prev = ant.previousNode();
                    node.updateHeuristic(prev, ant.destination, 1. / ant.totalTime);
                }
                if (ant.source == node.NODEID) continue; // Reached source
                int nxt = ant.nextNode();
                ant.nextHop = nxt;
                if (nodes.get(nxt).isOffline) continue; // Drop Ant
                for (Edge_ACO edge: adjList.get(node.NODEID)) {
                    if (edge.destination == nxt) {
                        edge.addAnt(ant, currentTime);
                        break;
                    }
                }
            } else { // Forward ant
                int nxt = node.nextHop(ant, alpha, beta, tabuSize);
                ant.nextHop = nxt;
                ant.addNode(nxt);
                ant.totalTime += (double) node.slowQ.size() / node.speed;
                if (ant.destination == node.NODEID) continue; // Reached destination
                if (nodes.get(nxt).isOffline) continue; // Drop Ant
                for (Edge_ACO edge : adjList.get(node.NODEID)) {
                    if (edge.destination == nxt) {
                        edge.addAnt(ant, currentTime);
                        break;
                    }
                }
            }
            throw new IllegalStateException();
        }
        while (!node.slowQ.isEmpty() && left-- > 0) {
            Packet packet = node.slowQ.poll();
            int nxt = node.nextHop(packet, alpha, beta, tabuSize);
            packet.nextHop = nxt;
            packet.addNode(nxt);
            if (packet.destination == node.NODEID) {
                ++success;
                continue;
            }
            if (nodes.get(nxt).isOffline) {
                ++failure;
                continue; // Drop Packet
            }
            for (Edge_ACO edge: adjList.get(node.NODEID)) {
                if (edge.destination == nxt) {
                    edge.addPacket(packet, currentTime);
                    break;
                }
            }
            throw new IllegalStateException();
        }
    }

    /**
     * Simulate edge
     *
     * @param edge Edge being processed
     */
    private void processEdge(Edge_ACO edge) {
        int left = edge.cost * SIM_SPEED;
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
        int amt = src.speed * SIM_SPEED;
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
        currentTime += SIM_SPEED;
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

package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
public class EACO extends AlgoBase {

    private int alpha, beta, ratio, TTL;
    private ArrayList<Node_EACO> nodes;
    private ArrayList<Edge_ACO> edgeList;

    /**
     * Initialize algorithm
     */
    public EACO() {

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
        nodes.add(new Node_EACO(numNodes++));
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
        edgeList.add(new Edge_ACO(node1, node2, cost));
        edgeList.add(new Edge_ACO(node2, node1, cost));
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
            edge.packets.clear();
            edge.ants.clear();
        }

    }
    /* Todo: Find a more efficient way to access edges */
    /**
     * Simulate node
     *
     * @param node Node being processed
     */
    private void processNode(Node_EACO node) {
        int left = node.speed;
        while (!node.fastQ.isEmpty() && left-- > 0) {
            Ant ant = node.fastQ.poll();
            if (ant.isBackwards) {
                /* Update Heuristics */
                /* Travel backwards */
                continue;
            }
            int nxt = node.nextHop(ant.destination, alpha, beta);
            ant.nextHop = nxt;
            ant.addNode(nxt);
            ant.totalTime += (double)node.slowQ.size() / node.speed;
            for (Edge_ACO edge: edgeList) {
                if (edge.source == node.NODEID && edge.destination == nxt) {
                    edge.addAnt(ant, currentTime);
                    break;
                }
            }
        }
        while (!node.slowQ.isEmpty() && left-- > 0) {
            Packet packet = node.slowQ.poll();
            int nxt = node.nextHop(packet.destination, alpha, beta);
            packet.nextHop = nxt;
            for (Edge_ACO edge: edgeList) {
                if (edge.source == node.NODEID && edge.destination == nxt) {
                    edge.addPacket(packet, currentTime);
                    break;
                }
            }
        }
    }

    /**
     * Simulate edge
     *
     * @param edge Edge being processed
     */
    private int processEdge(Edge_ACO edge) {
        int left = edge.cost * SIM_SPEED;
        int tot = 0;
        while (!edge.ants.isEmpty() && left > 0) {
            if (edge.ants.peek().timestamp > currentTime) break;
            Ant ant = edge.ants.poll();
            --left;
            if (nodes.get(ant.nextHop).isOffline) continue; // Drop this Ant
            if (ant.nextHop == destination) ++tot;
            else if (ant.decrementTTL()) nodes.get(ant.nextHop).fastQ.add(ant);
        }
        while (!edge.packets.isEmpty() && left > 0) {
            if (edge.packets.peek().timestamp > currentTime) break;
            Packet packet = edge.packets.poll();
            --left;
            if (nodes.get(packet.nextHop).isOffline) continue; // Drop this packet
            if (packet.nextHop == destination) ++tot;
            else if (packet.decrementTTL()) nodes.get(packet.nextHop).slowQ.add(packet);
        }
        return tot;
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
    /* Todo: Calculate reliability */
    /**
     * One tick of the simulation
     *
     * @return Throughput
     */
    int tick() {
        int tot = 0;
        currentTime += SIM_SPEED;
        generatePackets();
        for (Edge_ACO edge: edgeList) {
            if (edge.isOffline) continue;
            tot += processEdge(edge);
        }
        for (Node_EACO node: nodes) {
            if (node.isOffline) continue;
            processNode(node);
        }
        return tot;
    }
}

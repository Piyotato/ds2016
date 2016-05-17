package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
public class EACO extends AlgoBase {

    private int alpha, beta, ratio, TTL;
    private ArrayList<ArrayList<Integer>> adjList;
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
        nodes.add(new Node_EACO(numNodes++));
    }

    /**
     * Toggle state of a node
     * Toggles states of neighbouring edges
     *
     * @param ID Node ID
     */
    void toggleNode(int ID) {
        /* Perhaps ensure that source and destination are never offline */
        /* Perhaps ensure that the graph is always complete */
        Node_EACO node = nodes.get(ID);
        node.isOffline ^= true;
        if (node.isOffline) {
            //node.fastQ.clear();
            //node.slowQ.clear();
            for (int a = 0; a < edgeList.size(); ++a) {
                Edge_ACO edge = edgeList.get(a);
                if (edge.source != ID && edge.destination != ID) continue;
                if (edge.isOffline) continue;
                toggleEdge(a);
            }
        } else {
            for (int a = 0; a < edgeList.size(); ++a) {
                Edge_ACO edge = edgeList.get(a);
                if (edge.source != ID && edge.destination != ID) continue;
                if (!edge.isOffline) continue;
                toggleEdge(a);
            }
        }
    }

    /**
     * Add a bidirectional edge
     *
     * @param node1 First node
     * @param node2 Second node
     */
    void addEdge(int node1, int node2, int cost) {
        /* Perhaps check that the nodes exist */
        /* Perhaps check that there are no multi-edges */
        adjList.get(node1).add(node2);
        adjList.get(node2).add(node1);
        edgeList.add(new Edge_ACO(node1, node2, cost));
        edgeList.add(new Edge_ACO(node2, node1, cost));
    }

    /**
     * Toggle state of an edge
     *
     * @param ID Edge ID
     */
    void toggleEdge(int ID) {
        Edge_ACO edge = edgeList.get(ID);
        edge.isOffline ^= true;
        if (edge.isOffline) {
            adjList.get(edge.source).remove(edge.destination);
            adjList.get(edge.destination).remove(edge.source);
        } else {
            adjList.get(edge.source).add(edge.destination);
            adjList.get(edge.destination).add(edge.source);
        }
    }

    /**
     * Simulate node
     *
     * @param node Node being processed
     */
    private void processNode(Node_EACO node) {
        int left = node.speed;
        while (!node.fastQ.isEmpty() && left-- > 0) {
            Ant ant = node.fastQ.poll();
            int nxt = node.nextHop(ant.destination, alpha, beta);
            ant.nextHop = nxt;
            ant.path.add(nxt);
            /* Todo: Link queue depletion dynamics */
            Edge_ACO conn = null; /* Should never be null afterwards */
            for (Edge_ACO edge: edgeList) {
                if (edge.destination == nxt) {
                    conn = edge;
                    break;
                }
            }
            conn.addAnt(ant, currentTime);
        }
        while (!node.slowQ.isEmpty() && left-- > 0) {
            Packet packet = node.slowQ.poll();
            int nxt = node.nextHop(packet.destination, alpha, beta);
            packet.nextHop = nxt;
            Edge_ACO conn = null; /* Should never be null afterwards */
            for (Edge_ACO edge: edgeList) {
                if (edge.destination == nxt) {
                    conn = edge;
                    break;
                }
            }
            conn.addPacket(packet, currentTime);
        }
    }

    /**
     * Simulate edge
     *
     * @param edge Edge being processed
     */
    private int processEdge(Edge_ACO edge) {
        int left = edge.cost * SIMUL_SPEED;
        int tot = 0;
        while (!edge.ant.isEmpty() && left > 0) {
            if (edge.ant.peek().timestamp > currentTime) break;
            --left;
            Ant ant = edge.ant.poll();
            if (nodes.get(ant.nextHop).isOffline) continue; // Drop this Ant
            if (ant.nextHop == destination) ++tot;
            else if (ant.decrementTTL()) nodes.get(ant.nextHop).fastQ.add(ant);
        }
        while (!edge.packet.isEmpty() && left > 0) {
            if (edge.packet.peek().timestamp > currentTime) break;
            Packet packet = edge.packet.poll();
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

    /**
     * One tick of the simulation
     *
     * @return Throughput
     */
    int tick() {
        int tot = 0;
        currentTime += SIMUL_SPEED;
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

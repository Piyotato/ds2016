package com.ds2016;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by damian on 28/5/16.
 */
class Node_OSPF {

    final int ID;
    final HashMap<Integer, ArrayDeque<Packet>> Q = new HashMap<>();
    private final ArrayList<Node_OSPF> nodes;
    private final HashMap2D<Integer, Integer, Edge> adjMat;
    boolean isOffline;
    private Dijkstra SSSP;

    /**
     * Initialize a node
     *
     * @param _nodes  ArrayList of Node_OSPF
     * @param _adjMat Adjacency Matrix
     */
    Node_OSPF(ArrayList<Node_OSPF> _nodes, HashMap2D<Integer, Integer, Edge> _adjMat) {
        ID = _nodes.size();
        nodes = _nodes;
        adjMat = _adjMat;
    }

    /**
     * React to updates
     */
    void update() {
        SSSP = new Dijkstra(ID, nodes, adjMat);
    }

    /**
     * Calculate next best hop, for a
     * given destination
     *
     * @param packet Packet being processed
     * @return Neighbour for next hop
     */
    private int nextHop(Packet packet) {
        return SSSP.next(packet.destination);
    }

    /**
     * Process a packet
     *
     * @param packet Packet
     * @return 1, if packet has reached destination
     */
    int process(Packet packet) {
        if (packet.destination == ID) {
            return 1;
        }
        int nxt = nextHop(packet);
        if (!Q.containsKey(nxt)) Q.put(nxt, new ArrayDeque<>());
        Q.get(nxt).push(packet);
        return 0;
    }

    /**
     * Clear the packet queue
     */
    void clearQ() {
        Q.clear();
    }

    /**
     * Toggle isOffline
     */
    void toggle() {
        isOffline ^= true;
    }
}

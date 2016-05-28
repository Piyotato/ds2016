package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 28/5/16.
 */
class Node_OSPF extends Node {

    final ArrayDeque<Packet> Q = new ArrayDeque<>();

    private final ArrayList<Node_OSPF> nodes;
    private final HashMap2D<Integer, Integer, Edge> adjMat;
    private Dijkstra SSSP;

    /**
     * Initialize a node
     *
     * @param _speed Processing speed
     * @param _nodes ArrayList of Node_OSPF
     * @param _adjMat Adjacency Matrix
     */
    public Node_OSPF(int _speed, ArrayList<Node_OSPF> _nodes, HashMap2D<Integer, Integer, Edge> _adjMat) {
        super(_speed, _nodes.size());
        nodes = _nodes;
        adjMat = _adjMat;
        SSSP = new Dijkstra(nodeID, nodes, adjMat);
    }

    void update() {
        SSSP = new Dijkstra(nodeID, nodes, adjMat);
    }

    Integer nextHop(Packet packet) {
        return SSSP.B.get(packet.destination);
    }
}

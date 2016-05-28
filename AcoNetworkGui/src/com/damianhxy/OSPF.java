package com.damianhxy;

import java.util.*;
import javafx.util.*;

/**
 * Created by damian on 28/5/16.
 */
public class OSPF extends AlgorithmBase {

    /* Todo: Figure out TTL */

    private int success, failure;
    private final ArrayList<Node_OSPF> nodes = new ArrayList<>();
    private final ArrayList<Edge> edgeList = new ArrayList<>();
    private final HashMap2D<Integer, Integer, Edge> adjMat = new HashMap2D<>();

    /**
     * Initialize OSPF
     *
     * @param _source Source node
     * @param _destination Destination node
     */
    public OSPF(int _source, int _destination) {
        super(_source, _destination);
    }

    void addNode(int speed) {
        nodes.add(new Node_OSPF(speed, nodes, adjMat));
    }

    void toggleNode(int ID) throws IllegalArgumentException {
        if (ID == source || ID == destination) {
            throw new IllegalArgumentException();
        }
        Node_OSPF node = nodes.get(ID);
        node.isOffline ^= true;
        if (node.isOffline) {
            failure += node.Q.size();
            node.Q.clear();
            for (Edge edge: edgeList) {
                if (edge.source != ID || edge.destination != ID) continue;
                failure += edge.packets.size();
                edge.packets.clear();
            }
        }
        for (Node_OSPF _node: nodes) {
            _node.update();
        }
    }

    void addEdge(int node1, int node2, int cost) throws IllegalArgumentException {
        if (node1 >= nodes.size() || node2 >= nodes.size()) {
            throw new IllegalArgumentException();
        }
        Edge forward = new Edge(node1, node2, cost);
        Edge backward = new Edge(node2, node1, cost);
        edgeList.add(forward);
        edgeList.add(backward);
        adjMat.put(node1, node2, forward);
        adjMat.put(node2, node1, backward);
        for (Node_OSPF node: nodes) {
            node.update();
        }
    }

    void toggleEdge(int ID) {
        Edge forward = edgeList.get(ID * 2);
        Edge backward = edgeList.get(ID * 2 + 1);
        forward.isOffline ^= true;
        backward.isOffline ^= true;
        if (forward.isOffline) {
            failure += forward.packets.size();
            failure += backward.packets.size();
            forward.packets.clear();
            backward.packets.clear();
        }
        for (Node_OSPF node: nodes) {
            node.update();
        }
    }

    private void processNode(Node_OSPF node) {
        int left = node.speed;
        while (!node.Q.isEmpty() && left-- > 0) {
            Packet packet = node.Q.poll();
            if (packet.destination == node.nodeID) {
                ++success;
                continue;
            }
            int nxt = node.nextHop(packet); // Should never be null
            adjMat.get(node.nodeID, nxt).addPacket(packet, currentTime);
        }
    }

    private void processEdge(Edge edge) {
        while (!edge.packets.isEmpty()) {
            if (edge.packets.peek().timestamp >= currentTime) break;
            nodes.get(edge.destination).Q.add(edge.packets.poll());
        }
    }

    Pair<Integer, Integer> tick() {
        ++currentTime;
        for (Edge edge: edgeList) {
            if (edge.isOffline) continue;
            processEdge(edge);
        }
        for (Node_OSPF node: nodes) {
            if (node.isOffline) continue;
            processNode(node);
        }
        Pair<Integer, Integer> ret = new Pair<>(success, failure);
        success = failure = 0;
        return ret;
    }
}

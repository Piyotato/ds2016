package com.ds2016;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by damian on 28/5/16.
 */
public class AntNet implements AlgorithmBase {

    private final double alpha;
    private final int TTL, source, destination, interval;
    private final ArrayList<Edge_ACO> edgeList = new ArrayList<>();
    private final HashMap2D<Integer, Integer, Edge_ACO> adjMat = new HashMap2D<>();
    private final ArrayList<Node_AntNet> nodes = new ArrayList<>();
    private int success, failure, currentTime;

    /**
     * Initialize EACO
     *
     * @param _alpha Weightage of pheromone
     * @param _TTL Time To Live of packets
     * @param _interval Interval of Ant Generation
     * @param _source Source node
     * @param _destination Destination node
     */
    public AntNet(double _alpha, int _TTL, int _interval, int _source, int _destination) {
        alpha = _alpha;
        TTL = _TTL;
        source = _source;
        interval = _interval;
        destination = _destination;
    }

    /**
     * Retrieve the current load of the network's nodes
     *
     * @return Number of packets at each node
     */
    public ArrayList<Integer> getNodeStatus() {
        ArrayList<Integer> ret = new ArrayList<>();
        for (Node_AntNet node: nodes) {
            ret.add(node.slowQ.size());
        }
        return ret;
    }

    /**
     * Retrieve the current load of the network's edges
     *
     * @return Number of packets on each edge
     */
    public ArrayList<Integer> getEdgeStatus() {
        ArrayList<Integer> ret = new ArrayList<>();
        for (Edge_ACO edge: edgeList) {
            ret.add(edge.packets.size());
        }
        return ret;
    }

    /**
     * Add a new node
     *
     * @param speed Processing speed
     */
    public void addNode(int speed) {
        nodes.add(new Node_AntNet(speed, nodes, edgeList, adjMat, alpha));
    }

    /**
     * Toggle state of a node
     *
     * @param ID Node ID
     * @throws IllegalArgumentException
     */
    public void toggleNode(int ID) throws IllegalArgumentException {
        if (ID == source || ID == destination) {
            throw new IllegalArgumentException();
        }
        Node_AntNet node = nodes.get(ID);
        node.isOffline ^= true;
        if (node.isOffline) {
            node.fastQ.clear();
            node.slowQ.clear();
            for (Edge_ACO edge: edgeList) {
                if (edge.source != ID && edge.destination != ID) continue;
                edge.packets.clear();
                edge.ants.clear();
            }
        }
        for (Node_AntNet _node: nodes) {
            _node.toggleNode(ID);
        }
    }

    /**
     * Add a bidirectional edge
     *
     * @param node1 First node
     * @param node2 Second node
     * @param cost Time taken
     * @throws IllegalArgumentException
     */
    public void addEdge(int node1, int node2, int cost) throws IllegalArgumentException {
        if (node1 >= nodes.size() || node2 >= nodes.size()) {
            throw new IllegalArgumentException();
        }
        Edge_ACO forward = new Edge_ACO(node1, node2, cost);
        Edge_ACO backward = new Edge_ACO(node2, node1, cost);
        edgeList.add(forward);
        edgeList.add(backward);
        adjMat.put(node1, node2, forward);
        adjMat.put(node2, node1, backward);
        for (Node_ACO node: nodes) {
            node.addEdge(node1, node2);
        }
    }

    /**
     * Toggle state of an edge
     *
     * @param ID Edge ID
     */
    public void toggleEdge(int ID) {
        Edge_ACO forward = edgeList.get(ID * 2);
        Edge_ACO backward = edgeList.get(ID * 2 + 1);
        forward.isOffline ^= true;
        backward.isOffline ^= true;
        if (forward.isOffline) {
            forward.packets.clear();
            forward.ants.clear();
            backward.packets.clear();
            backward.ants.clear();
        }
        for (Node_ACO node: nodes) {
            node.toggleEdge(ID * 2);
            node.toggleEdge(ID * 2 + 1);
        }
    }

    /**
     * Simulate node
     *
     * @param node Node being processed
     */
    private void processNode(Node_AntNet node) {
        int left = node.speed;
        while (!node.fastQ.isEmpty() && left-- > 0) {
            Ant ant = node.fastQ.poll();
            if (ant.isBackwards) { // Backward ant
                ant.updateTotalTime();
                int prev = ant.previousNode();
                Double P = node.pheromone.get(ant.destination, prev);
                if (P == null) {
                    continue; // This path is no longer viable
                }
                double R = 1. / ant.totalTime;
                double change = (P * (1 - R) + R) - P;
                node.updateHeuristic(prev, ant.destination, change);
                if (ant.source == node.nodeID) {
                    continue; // Reached source
                }
                int nxt = ant.nextNode();
                if (nodes.get(nxt).isOffline || adjMat.get(node.nodeID, nxt).isOffline) {
                    continue; // Path is gone
                }
                adjMat.get(node.nodeID, nxt).addAnt(ant, currentTime);
            } else { // Forward ant
                Integer nxt;
                if (ant.destination == node.nodeID) {
                    ant.isBackwards = true;
                    nxt = ant.nextNode();
                    ant.timings.add((double)node.slowQ.size() / node.speed); // Time spent on this node
                    adjMat.get(node.nodeID, nxt).addAnt(ant, currentTime);
                } else if (ant.isValid(currentTime)) {
                    nxt = node.antNextHop(ant);
                    if (nxt == null) {
                        continue; // Too large a cycle
                    }
                    ant.addNode(node.nodeID); // Add current node to path
                    if (nxt >= 0) { // If there was no cycle
                        ant.timings.add((double) node.slowQ.size() / node.speed); // Time spent on this node
                        ant.timings.add((double) adjMat.get(node.nodeID, nxt).cost);
                    } else {
                        nxt = -nxt;
                    }
                    adjMat.get(node.nodeID, nxt).addAnt(ant, currentTime);
                }
            }
        }
        while (!node.slowQ.isEmpty() && left-- > 0) {
            Packet packet = node.slowQ.poll();
            if (packet.destination == node.nodeID) {
                ++success;
                continue;
            } else if (!packet.isValid(currentTime)) {
                ++failure;
                continue;
            }
            int nxt = node.packetNextHop(packet);
            adjMat.get(node.nodeID, nxt).addPacket(packet, currentTime);
        }
    }

    /**
     * Simulate edge
     *
     * @param edge Edge being processed
     */
    private void processEdge(Edge_ACO edge) {
        while (!edge.ants.isEmpty()) {
            if (edge.ants.peek().timestamp > currentTime) break;
            nodes.get(edge.destination).fastQ.add(edge.ants.poll());
        }
        while (!edge.packets.isEmpty()) {
            if (edge.packets.peek().timestamp > currentTime) break;
            nodes.get(edge.destination).slowQ.add(edge.packets.poll());
        }
    }

    /**
     * Generate packets from source
     */
    private void generatePackets() {
        Node_AntNet src = nodes.get(source);
        // Send packets from source node
        for (int a = 0; a < src.speed - 1; ++a) {
            src.slowQ.add(new Packet(source, destination, TTL, currentTime));
        }
        // Send ants from all nodes
        if (currentTime % interval == 0) {
            for (Node_AntNet node: nodes) {
                node.fastQ.add(new Ant(node.nodeID, destination, TTL, currentTime));
            }
        }
    }

    /**
     * One tick of the simulation
     *
     * @return Success, Failure
     */
    public Pair<Integer, Integer> tick() {
        ++currentTime;
        generatePackets();
        for (Edge_ACO edge: edgeList) {
            if (edge.isOffline) continue;
            processEdge(edge);
        }
        for (Node_AntNet node: nodes) {
            if (node.isOffline) continue;
            processNode(node);
        }
        Pair<Integer, Integer> ret = new Pair<>(success, failure);
        success = failure = 0;
        return ret;
    }
}

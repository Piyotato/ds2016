package com.ds2016;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by damian on 28/5/16.
 */
public class AntNet implements AlgorithmBase {

    public final ArrayList<Node_AntNet> nodes = new ArrayList<>();
    private final double alpha, interval;
    private final int TTL, traffic;
    private final ArrayList<Edge_ACO> edgeList = new ArrayList<>();
    private final HashMap2D<Integer, Integer, Edge_ACO> adjMat = new HashMap2D<>();
    private int source, destination;
    private int success, currentTime, numAntsGen;
    private boolean didInit;

    /**
     * Initialize EACO
     *
     * @param _alpha    Weightage of pheromone
     * @param _traffic  Packets per tick
     * @param _TTL      Time To Live of packets
     * @param _interval Interval of Ant Generation
     */
    public AntNet(double _alpha, int _traffic, int _TTL, double _interval) {
        alpha = _alpha;
        traffic = _traffic;
        TTL = _TTL;
        interval = _interval;
    }

    /**
     * Initialize AntNet
     *
     * @param _source      Source node
     * @param _destination Destination node
     */
    public void init(int _source, int _destination) {
        currentTime = 0;
        source = _source;
        destination = _destination;
        for (Node_AntNet node : nodes) {
            node.init();
        }
        didInit = true;
    }

    /**
     * Retrieve the current load of the network's edges
     *
     * @return Number of packets on each edge
     */
    public ArrayList<Integer> getEdgeStatus() {
        ArrayList<Integer> ret = new ArrayList<>();
        for (Edge_ACO edge : edgeList) {
            ret.add(edge.packets.size());
        }
        return ret;
    }

    /**
     * Add a new node
     */
    public void addNode() {
        nodes.add(new Node_AntNet(nodes, adjMat, alpha));
    }

    /**
     * Toggle state of a node
     *
     * @param ID Node ID
     * @throws IllegalArgumentException if ID is out of bounds
     */
    public void toggleNode(int ID) throws IllegalArgumentException {
        if (ID == source || ID == destination || ID >= nodes.size()) {
            throw new IllegalArgumentException();
        }
        Node_AntNet node = nodes.get(ID);
        node.toggle();
        if (node.isOffline) {
            node.clearFastQ();
            node.clearSlowQ();
            for (Edge_ACO edge : edgeList) {
                if (edge.source != ID && edge.destination != ID) continue;
                edge.clearPacketQ();
                edge.clearAntQ();
            }
        }
        if (didInit)
            for (Edge_ACO edge : adjMat.get(ID).values()) {
                nodes.get(edge.destination).toggleNode(ID);
            }
    }

    /**
     * Add a bidirectional edge
     *
     * @param node1     First node
     * @param node2     Second node
     * @param cost      Time taken
     * @param bandwidth Packets per tick
     * @throws IllegalArgumentException if ID is out of bounds
     */
    public void addEdge(int node1, int node2, int cost, int bandwidth) throws IllegalArgumentException {
        if (node1 >= nodes.size() || node2 >= nodes.size()) {
            throw new IllegalArgumentException();
        }
        Edge_ACO forward = new Edge_ACO(node1, node2, cost, bandwidth);
        Edge_ACO backward = new Edge_ACO(node2, node1, cost, bandwidth);
        edgeList.add(forward);
        edgeList.add(backward);
        adjMat.put(node1, node2, forward);
        adjMat.put(node2, node1, backward);
        if (didInit) {
            nodes.get(node1).addEdge(node2);
            nodes.get(node2).addEdge(node1);
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
        forward.toggle();
        backward.toggle();
        if (forward.isOffline) {
            forward.clearAntQ();
            forward.clearPacketQ();
            backward.clearAntQ();
            backward.clearPacketQ();
        }
        if (didInit) {
            nodes.get(forward.source).toggleEdge(forward.destination);
            nodes.get(forward.destination).toggleEdge(forward.source);
        }
    }

    /**
     * Simulate node
     *
     * @param node Node being processed
     */
    private void processNode(Node_AntNet node) {
        HashMap<Integer, Integer> bandwidth = new HashMap<>();
        // Process Ants
        for (Integer neighbour : node.fastQ.keySet()) {
            ArrayDeque<Ant> Q = node.fastQ.get(neighbour);
            Edge_ACO edge = adjMat.get(node.ID, neighbour);
            bandwidth.put(neighbour, edge.bandwidth);
            while (!Q.isEmpty() && bandwidth.get(neighbour) > 0) {
                edge.addAnt(Q.poll(), currentTime);
                bandwidth.put(neighbour, bandwidth.get(neighbour) - 1);
            }
        }
        // Process Packets
        for (Integer neighbour : node.slowQ.keySet()) {
            ArrayDeque<Packet> Q = node.slowQ.get(neighbour);
            Edge_ACO edge = adjMat.get(node.ID, neighbour);
            if (!bandwidth.containsKey(neighbour))
                bandwidth.put(neighbour, edge.bandwidth);
            while (!Q.isEmpty() && bandwidth.get(neighbour) > 0) {
                edge.addPacket(Q.poll(), currentTime);
                bandwidth.put(neighbour, bandwidth.get(neighbour) - 1);
            }
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
            if (!edge.ants.peek().isValid(currentTime)) {
                edge.ants.poll();
                continue;
            }
            nodes.get(edge.destination).processAnt(edge.ants.poll());
        }
        while (!edge.packets.isEmpty()) {
            if (edge.packets.peek().timestamp > currentTime) break;
            if (!edge.packets.peek().isValid(currentTime)) {
                edge.packets.poll();
                continue;
            }
            success += nodes.get(edge.destination).processPacket(edge.packets.poll());
        }
    }

    /**
     * Generate packets from source
     */
    private void generatePackets() {
        int curNumAnts = (int) ((currentTime * 100) / (interval * 1000));
        for (Node_AntNet node : nodes) {
            if (node.isOffline) continue;
            for (int cnt = 0; numAntsGen + cnt < curNumAnts; ++cnt) {
                node.processAnt(new Ant(node.ID, getRandomNode(node.ID), TTL, currentTime));
            }
        }
        numAntsGen = curNumAnts;
        Node_AntNet src = nodes.get(source);
        // Send packets from source node
        for (int cnt = 0; cnt < traffic; ++cnt) {
            src.processPacket(new Packet(source, destination, TTL, currentTime));
        }
    }

    /**
     * Generate a random destination for ants
     * Beware of infinite loop when all other nodes are offline!
     *
     * @param currentNode Node to exclude
     * @return Node ID
     */
    private int getRandomNode(int currentNode) {
        Random rand = new Random();
        int randomNode = rand.nextInt(nodes.size());
        while (randomNode == currentNode || nodes.get(randomNode).isOffline) {
            randomNode = rand.nextInt(nodes.size());
        }
        return randomNode;
    }

    /**
     * One tick of the simulation
     *
     * @return Number of packets that reached their destination
     */
    public int tick() {
        success = 0;
        ++currentTime;
        for (Edge_ACO edge : edgeList) {
            if (edge.isOffline) continue;
            processEdge(edge);
        }
        generatePackets();
        for (Node_AntNet node : nodes) {
            if (node.isOffline) continue;
            processNode(node);
        }
        return success;
    }

    /**
     * Build graph from supplied information
     *
     * @param _nodes       Node_GUI nodes
     * @param _edgeList    SimpleEdge edges
     * @param _source      Source node
     * @param _destination Destination node
     */
    public void build(ArrayList<Node_GUI> _nodes, ArrayList<SimpleEdge> _edgeList, int _source, int _destination) {
        nodes.clear();
        edgeList.clear();
        adjMat.clear();
        // Node
        for (Node_GUI node : _nodes) {
            nodes.add(new Node_AntNet(nodes, adjMat, alpha));
            if (node.isOffline) {
                nodes.get(nodes.size() - 1).toggle();
            }
        }
        // Edges
        for (SimpleEdge edge : _edgeList) {
            Edge_ACO forward = new Edge_ACO(edge.source, edge.destination, edge.cost, edge.bandwidth);
            Edge_ACO backward = new Edge_ACO(edge.destination, edge.source, edge.cost, edge.bandwidth);
            if (edge.isOffline) {
                forward.toggle();
                backward.toggle();
            }
            edgeList.add(forward);
            edgeList.add(backward);
            adjMat.put(edge.source, edge.destination, forward);
            adjMat.put(edge.destination, edge.source, backward);
        }
        init(_source, _destination);
    }
}

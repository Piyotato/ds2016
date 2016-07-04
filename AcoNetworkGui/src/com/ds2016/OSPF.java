package com.ds2016;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by damian on 28/5/16.
 */
class OSPF implements AlgorithmBase {

    private final int TTL;
    private final ArrayList<Node_OSPF> nodes = new ArrayList<>();
    private final ArrayList<Edge> edgeList = new ArrayList<>();
    private final HashMap2D<Integer, Integer, Edge> adjMat = new HashMap2D<>();
    private int source, destination;
    private int success, failure, currentTime, packetCnt;

    /**
     * Initialize OSPF
     *
     * @param _TTL Time To Live of packets
     */
    public OSPF(int _TTL) {
        TTL = _TTL;
    }

    /**
     * Initialize OSPF
     *
     * @param _source      Source node
     * @param _destination Destination node
     */
    public void init(int _source, int _destination) {
        source = _source;
        destination = _destination;
        for (Node_OSPF node : nodes) {
            node.update();
        }
    }

    /**
     * Retrieve the current load of the network's nodes
     *
     * @return Number of packets at each node
     */
    public ArrayList<Integer> getNodeStatus() {
        ArrayList<Integer> ret = new ArrayList<>();
        for (Node_OSPF node : nodes) {
            ret.add(node.Q.size());
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
        for (Edge edge : edgeList) {
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
        nodes.add(new Node_OSPF(speed, nodes, adjMat));
    }

    /**
     * Toggle state of anode
     *
     * @param ID Node ID
     * @throws IllegalArgumentException
     */
    public void toggleNode(int ID) throws IllegalArgumentException {
        if (ID == source || ID == destination) {
            throw new IllegalArgumentException();
        }
        Node_OSPF node = nodes.get(ID);
        node.isOffline ^= true;
        if (node.isOffline) {
            packetCnt -= node.Q.size();
            node.Q.clear();
            for (Edge edge : adjMat.get(ID).values()) {
                packetCnt -= edge.packets.size();
                edge.packets.clear();
            }
        }
        for (Node_OSPF _node : nodes) {
            _node.update();
        }
    }

    /**
     * Add a bidirectional edge
     *
     * @param node1 First node
     * @param node2 Second node
     * @param cost  Time taken
     * @throws IllegalArgumentException
     */
    public void addEdge(int node1, int node2, int cost) throws IllegalArgumentException {
        if (node1 >= nodes.size() || node2 >= nodes.size()) {
            throw new IllegalArgumentException();
        }
        Edge forward = new Edge(node1, node2, cost);
        Edge backward = new Edge(node2, node1, cost);
        edgeList.add(forward);
        edgeList.add(backward);
        adjMat.put(node1, node2, forward);
        adjMat.put(node2, node1, backward);
        for (Node_OSPF node : nodes) {
            node.update();
        }
    }

    /**
     * Toggle state of an edge
     *
     * @param ID Edge ID
     */
    public void toggleEdge(int ID) {
        Edge forward = edgeList.get(ID * 2);
        Edge backward = edgeList.get(ID * 2 + 1);
        forward.isOffline ^= true;
        backward.isOffline ^= true;
        if (forward.isOffline) {
            packetCnt -= forward.packets.size();
            packetCnt -= backward.packets.size();
            forward.packets.clear();
            backward.packets.clear();
        }
        for (Node_OSPF node : nodes) {
            node.update();
        }
    }

    /**
     * Simulate node
     *
     * @param node Node being processed
     */
    private void processNode(Node_OSPF node) {
        int left = node.speed;
        while (!node.Q.isEmpty() && left-- > 0) {
            Packet packet = node.Q.poll();
            if (packet.destination == node.nodeID) {
                ++success;
                --packetCnt;
                continue;
            } else if (!packet.isValid(currentTime)) {
                ++failure;
                --packetCnt;
                ++left; // "Skip" this packet
                continue;
            }
            int nxt = node.nextHop(packet);
            packet.timestamp = currentTime + adjMat.get(node.nodeID, nxt).cost;
            if (!packet.isValid(packet.timestamp) && nxt != packet.destination) {
                ++failure;
                --packetCnt;
                continue; // Would expire before reaching
            }
            adjMat.get(node.nodeID, nxt).addPacket(packet, currentTime);
        }
    }

    /**
     * Simulate edge
     *
     * @param edge Edge being processed
     */
    private void processEdge(Edge edge) {
        while (!edge.packets.isEmpty()) {
            if (edge.packets.peek().timestamp >= currentTime) break;
            nodes.get(edge.destination).Q.add(edge.packets.poll());
        }
    }

    /**
     * Generate packets from source
     */
    private void generatePackets() {
        Node_OSPF src = nodes.get(source);
        packetCnt += Math.max(src.speed - src.Q.size(), 0);
        while (src.Q.size() < src.speed) {
            src.Q.add(new Packet(source, destination, TTL, currentTime));
        }
    }

    /**
     * One tick of the simulation
     *
     * @return Success, Failure
     */
    public Pair<Integer, Integer> tick() {
        ++currentTime;
        for (Edge edge : edgeList) {
            if (edge.isOffline) continue;
            processEdge(edge);
        }
        generatePackets();
        for (Node_OSPF node : nodes) {
            if (node.isOffline) continue;
            processNode(node);
        }
        Pair<Integer, Integer> ret = new Pair<>(success, failure);
        success = failure = 0;
        return ret;
    }


    /**
     * Count the number of packets
     * that will eventually expire
     *
     * @return Number of packets
     */
    public Pair<Integer, Integer> terminate() {
        while (packetCnt > 0) {
            ++currentTime;
            for (Edge edge : edgeList) {
                if (edge.isOffline) continue;
                processEdge(edge);
            }
            for (Node_OSPF node : nodes) {
                if (node.isOffline) continue;
                processNode(node);
            }
        }
        return new Pair<>(success, failure);
    }

    /**
     * Build sGraph from supplied information
     *
     * @param _nodes       Node_GUI nodes
     * @param _edgeList    SimpleEdge edges
     * @param _source      Source node
     * @param _destination Destination node
     */
    public void build(ArrayList<Node_GUI> _nodes, ArrayList<SimpleEdge> _edgeList, int _source, int _destination) {
        currentTime = 0;
        nodes.clear();
        edgeList.clear();
        adjMat.clear();
        // Node
        for (Node_GUI node : _nodes) {
            nodes.add(new Node_OSPF(node.speed, nodes, adjMat));
            if (node.isOffline) {
                nodes.get(nodes.size() - 1).isOffline = true;
            }
        }
        // Edge
        for (SimpleEdge edge : _edgeList) {
            Edge forward = new Edge(edge.source, edge.destination, edge.cost);
            Edge backward = new Edge(edge.destination, edge.source, edge.cost);
            edgeList.add(forward);
            edgeList.add(backward);
            if (edge.isOffline) {
                forward.isOffline = true;
                backward.isOffline = true;
            }
            adjMat.put(edge.source, edge.destination, forward);
            adjMat.put(edge.destination, edge.source, backward);
        }
        init(_source, _destination);
    }
}

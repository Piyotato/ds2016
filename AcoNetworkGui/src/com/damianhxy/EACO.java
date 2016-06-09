package com.damianhxy;

import java.util.*;
import javafx.util.*;

/**
 * Created by damian on 16/5/16.
 */
public class EACO extends AntNet {

    private final ArrayList<Node_EACO> nodes = new ArrayList<>();

    /**
     * Initialize EACO
     *
     * @param _alpha Weightage of pheromone
     * @param _beta Weightage of cost function
     * @param _ratio Ratio of ants to packets
     * @param _TTL Time To Live of packets
     * @param _tabuSize Size of tabu list
     * @param _source Source node
     * @param _destination Destination node
     */
    public EACO(int _alpha, int _beta, int _ratio, int _TTL, int _tabuSize, int _source, int _destination) {
        super(_alpha, _beta, _ratio, _TTL, _tabuSize, _source, _destination);
    }

    /**
     * Add a new node
     *
     * @param speed Processing speed
     */
    public void addNode(int speed) {
        nodes.add(new Node_EACO(speed, nodes, edgeList, adjMat, alpha, beta, tabuSize));
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
        Node_EACO node = nodes.get(ID);
        node.isOffline ^= true;
        if (node.isOffline) {
            failure += node.slowQ.size();
            node.fastQ.clear();
            node.slowQ.clear();
            for (Edge_ACO edge: edgeList) {
                if (edge.source != ID && edge.destination != ID) continue;
                failure += edge.packets.size();
                edge.packets.clear();
                edge.ants.clear();
            }
        }
        for (Node_EACO _node: nodes) {
            _node.toggleNode(ID);
        }
    }

    /* Inherits AntNet.addEdge() */

    /* Inherits AntNet.toggleEdge() */

    /**
     * Simulate node
     *
     * @param node Node being processed
     */
    private void processNode(Node_EACO node) {
        int left = node.speed;
        while (!node.fastQ.isEmpty() && left-- > 0) {
            Ant ant = node.fastQ.poll();
            if (ant.isBackwards) { // Backward ant
                ant.updateTotalTime();
                int prev = ant.previousNode();
                Double P = node.pheromone.get(ant.destination, prev);
                if (P == null) continue; // This path is no longer viable
                double R = 1. / ant.totalTime;
                double change = (P * (1 - R) + R) - P;
                node.updateHeuristic(prev, ant.destination, change);
                if (ant.source == node.nodeID) continue; // Reached source
                int nxt = ant.nextNode();
                if (nodes.get(nxt).isOffline) continue; // Drop Ant
                adjMat.get(node.nodeID, nxt).addAnt(ant, currentTime);
            } else { // Forward ant
                ant.timings.add((double)node.slowQ.size() / node.speed);
                Integer nxt;
                if (ant.destination == node.nodeID) {
                    ant.isBackwards = true;
                    nxt = ant.nextNode();
                } else if (ant.decrementTTL()) {
                    nxt = node.nextHop(ant);
                    if (nxt == null) continue; // Drop Ant
                    ant.addNode(nxt);
                    ant.timings.add((double)adjMat.get(node.nodeID, nxt).cost);
                } else continue;
                adjMat.get(node.nodeID, nxt).addAnt(ant, currentTime);
            }
        }
        while (!node.slowQ.isEmpty() && left-- > 0) {
            Packet packet = node.slowQ.poll();
            if (packet.destination == node.nodeID) {
                ++success;
                continue;
            } else if (!packet.decrementTTL()) {
                ++failure;
                continue;
            }
            Integer nxt = node.nextHop(packet);
            if (nxt == null) {
                ++failure;
                continue; // Drop packet
            }
            packet.addNode(nxt);
            adjMat.get(node.nodeID, nxt).addPacket(packet, currentTime);
        }
    }

    /* Inherits AntNet.processEdge() */

    /**
     * Generate packets from source
     */
    private void generatePackets() {
        Node_EACO src = nodes.get(source);
        int amt = src.speed * currentTime;
        int totPackets = ratio * amt / (ratio + 1);
        int totAnts = amt / (ratio + 1);
        for (; numPackets < totPackets; ++numPackets) {
            src.slowQ.add(new Packet(source, destination, TTL));
        }
        for (; numAnts < totAnts; ++numAnts) {
            src.fastQ.add(new Ant(source, destination, TTL));
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
        for (Node_EACO node: nodes) {
            if (node.isOffline) continue;
            processNode(node);
        }
        Pair<Integer, Integer> ret = new Pair<>(success, failure);
        success = failure = 0;
        return ret;
    }
}

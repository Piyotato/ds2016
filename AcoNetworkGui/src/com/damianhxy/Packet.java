package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Packet {

    int source, destination, TTL, nextHop, timestamp;
    ArrayList<Integer> path;

    /**
     * Initializes a packet
     *
     * @param _source Source node
     * @param _destination Destination node
     * @param _TTL Time to live
     */
    Packet(int _source, int _destination, int _TTL) {
        source = _source;
        destination = _destination;
        TTL = _TTL;
    }

    /**
     * Decrements TTL and checks expiration
     *
     * @return Whether packet is still valid
     */
    boolean decrementTTL() {
        return --TTL != 0;
    }

    /**
     * Add a node to the path
     * Assumes that node is valid
     *
     * @param node Current node
     */
    void addNode(int node) {
        path.add(node);
    }

    /**
     * Check if a node is currently
     * blacklisted in the Tabu list
     *
     * @param node Neighbouring node
     * @param tabuSize Size of Tabu list
     * @return Whether node is valid
     */
    boolean isValid(int node, int tabuSize) {
        for (int a = 0; a < Math.min(tabuSize, path.size()); ++a) {
            if (path.get(path.size() - a - 1) == node) {
                return false;
            }
        }
        return true;
    }
}

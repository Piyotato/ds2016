package com.ds2016;

import java.util.HashSet;

/**
 * Created by damian on 16/5/16.
 */
class Packet {

    final int source, destination;
    final HashSet<Integer> tabuList = new HashSet<>();
    private final int creation;
    private final int TTL;
    int timestamp;

    /**
     * Initializes a packet
     *
     * @param _source      Source node
     * @param _destination Destination node
     * @param _TTL         Time to live
     * @param _creation    Time of creation
     */
    Packet(int _source, int _destination, int _TTL, int _creation) {
        source = _source;
        destination = _destination;
        TTL = _TTL;
        creation = _creation;
    }

    /**
     * Checks expiration
     *
     * @param _time Current time
     * @return Whether packet is still valid
     */
    boolean isValid(int _time) {
        return (_time - creation) <= TTL;
    }

    /**
     * Check if a node is
     * currently in the Tabu list
     *
     * @param node Neighbouring node
     * @return Whether node is valid
     */
    boolean canVisit(int node) {
        return !tabuList.contains(node);
    }
}

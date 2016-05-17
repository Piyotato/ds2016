package com.damianhxy;

/**
 * Created by damian on 16/5/16.
 */
class Packet {

    int source, destination, TTL, nextHop, timestamp;

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

}

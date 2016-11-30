package com.ds2016;

/**
 * Created by damian on 16/5/16.
 */
class Packet {

    final int source, destination;
    private final int TTL, creation;
    int timestamp;

    /**
     * Initializes a packet
     *
     * @param _source      Source node
     * @param _destination Destination node
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
     * @param time Current time
     * @return Whether packet is still valid
     */
    boolean isValid(int time) {
        return (time - creation) <= TTL;
    }
}

package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 17/5/16.
 */
class Edge extends SimpleEdge {

    final Queue<Packet> packets = new ArrayDeque<>();

    /**
     * Initializes an edge
     *
     * @param _source Start node
     * @param _destination End node
     * @param _cost Time taken to traverse
     */
    Edge(int _source, int _destination, int _cost) {
        super(_source, _destination, _cost);
    }

    /**
     * Transmit a packet
     *
     * @param packet Packet
     * @param currentTime Timestamp
     */
    void addPacket(Packet packet, int currentTime) {
        packet.timestamp = currentTime + cost;
        packets.add(packet);
    }
}

package com.ds2016;

import java.util.ArrayDeque;

/**
 * Created by damian on 17/5/16.
 */
class Edge extends SimpleEdge {

    final ArrayDeque<Packet> packets = new ArrayDeque<>();

    /**
     * Initializes an edge
     *
     * @param source      Start node
     * @param destination End node
     * @param cost        Time taken to traverse
     */
    Edge(int source, int destination, int cost) {
        super(source, destination, cost);
    }

    /**
     * Transmit a packet
     *
     * @param packet      Packet
     * @param currentTime Timestamp
     */
    void addPacket(Packet packet, int currentTime) {
        packet.timestamp = currentTime + cost;
        packets.add(packet);
    }
}

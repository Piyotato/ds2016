package com.damianhxy;

/**
 * Created by damian on 27/5/16.
 */
abstract class Node {

    final int speed, nodeID;
    boolean isOffline;

    Node(int _speed, int _nodeID) {
        speed = _speed;
        nodeID = _nodeID;
    }

    abstract Integer nextHop(Packet packet);
}

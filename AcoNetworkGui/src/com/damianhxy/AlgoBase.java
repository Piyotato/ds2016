package com.damianhxy;

/**
 * Created by damian on 17/5/16.
 */
class AlgoBase {

    final static int SIM_SPEED = 1; // Simulated Time : Real Time
    int numNodes, currentTime;
    final int source, destination;

    /**
     * Initialize algorithm
     *
     * @param _source Source node
     * @param _destination Destination node
     */
    AlgoBase(int _source, int _destination) {
        source = _source;
        destination = _destination;
    }
}

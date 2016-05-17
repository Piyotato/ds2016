package com.damianhxy;

/**
 * Created by damian on 17/5/16.
 */
class SimpleEdge {

    int source, destination, cost;
    boolean isOffline;

    /**
     * Initializes an edge
     *
     * @param _source Startpoint
     * @param _destination Endpoint
     * @param _cost Time taken to traverse
     */
    SimpleEdge(int _source, int _destination, int _cost) {
        source = _source;
        destination = _destination;
        cost = _cost;
    }
}

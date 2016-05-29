package com.damianhxy;

/**
 * Created by damian on 27/5/16.
 */
interface Node_ACO {

    double EPS = 1e-5;

    void toggleNode(int ID);

    void addEdge(int node1, int node2);

    void toggleEdge(int ID);

    Integer nextHop(Packet packet);
}

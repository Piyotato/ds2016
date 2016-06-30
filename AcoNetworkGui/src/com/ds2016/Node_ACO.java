package com.ds2016;

/**
 * Created by damian on 27/5/16.
 */
interface Node_ACO {

    double EPS = 1e-5;

    void addEdge(int node1, int node2);

    void toggleEdge(int ID);

    Integer antNextHop(Ant ant);

    int packetNextHop(Packet packet);
}

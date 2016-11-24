package com.ds2016;

import java.util.ArrayList;

/**
 * Created by damian on 17/5/16.
 */
public interface AlgorithmBase {

    ArrayList<Integer> getEdgeStatus();

    void addNode();

    void toggleNode(int ID);

    void addEdge(int node1, int node2, int cost, int bandwidth);

    void toggleEdge(int ID);

    int tick();

    void build(ArrayList<Node_GUI> nodes, ArrayList<SimpleEdge> edgeList, int source, int destination);

    void init(int source, int destination);
}

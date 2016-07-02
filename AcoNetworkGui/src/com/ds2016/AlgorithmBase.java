package com.ds2016;

import javafx.util.*;

import java.util.ArrayList;

/**
 * Created by damian on 17/5/16.
 */
interface AlgorithmBase {

    ArrayList<Integer> getNodeStatus();

    ArrayList<Integer> getEdgeStatus();

    void addNode(int speed);

    void toggleNode(int ID);

    void addEdge(int node1, int node2, int cost);

    void toggleEdge(int ID);

    Pair<Integer, Integer> tick();

    void build(ArrayList<Node_GUI> nodes, ArrayList<SimpleEdge> edgeList, int source, int destination);

    void init(int source, int destination);

    Pair<Integer, Integer> terminate();
}

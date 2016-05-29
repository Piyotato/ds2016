package com.damianhxy;

import javafx.util.*;

/**
 * Created by damian on 17/5/16.
 */
interface AlgorithmBase {

    void addNode(int speed);

    void toggleNode(int ID);

    void addEdge(int node1, int node2, int cost);

    void toggleEdge(int ID);

    Pair<Integer, Integer> tick();
}

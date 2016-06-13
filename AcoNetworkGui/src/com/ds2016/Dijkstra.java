package com.ds2016;

import javafx.util.*;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by damian on 28/5/16.
 */
class Dijkstra {

    private final static int INF = 1000000000;
    final ArrayList<Integer> B = new ArrayList<>();
    private final ArrayList<Integer> P = new ArrayList<>();
    private final int source;

    /**
     * Initialize Dijkstra data structure
     *
     * @param _source Source node
     * @param _nodes ArrayList of Node_OSPF
     * @param _adjMat Adjacency Matrix
     */
    Dijkstra(int _source, ArrayList<Node_OSPF> _nodes, HashMap2D<Integer, Integer, Edge> _adjMat) {
        final ArrayList<Integer> D = new ArrayList<>();
        final PriorityQueue<Pair<Integer, Integer>> PQ = new PriorityQueue<>();
        for (int a = 0; a < _nodes.size(); ++a) {
            D.add(INF);
            P.add(-1);
            B.add(-1);
        }
        source = _source;
        D.set(_source, 0);
        PQ.add(new Pair<>(0, _source));
        while (!PQ.isEmpty()) {
            Pair<Integer, Integer> top = PQ.poll();
            if (!D.get(top.getValue()).equals(top.getKey())) continue;
            for (Edge edge: _adjMat.get(top.getValue()).values()) {
                if (edge.isOffline) continue;
                if (_nodes.get(edge.destination).isOffline) continue;
                int nc = top.getKey() + edge.cost;
                if (nc < D.get(edge.destination)) {
                    D.set(edge.destination, nc);
                    P.set(edge.destination, top.getValue());
                    PQ.add(new Pair<>(nc, edge.destination));
                }
            }
        }
        for (int a = 0; a < _nodes.size(); ++a) {
            if (a == _source) continue;
            if (B.get(a) != -1) {
                B.set(a, calc(a));
            }
        }
    }

    /**
     * Calculates best neighbour from source
     * to reach given destination
     *
     * @param node Node ID
     * @return Best neighbour node ID
     */
    private int calc(int node) {
        if (B.get(node) != -1) return B.get(node);
        if (P.get(node).equals(-1)) return -1; // Graph is not complete yet
        if (P.get(node).equals(source)) return node;
        B.set(node, calc(P.get(node)));
        return B.get(node);
    }
}

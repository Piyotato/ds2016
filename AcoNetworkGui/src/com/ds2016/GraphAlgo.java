package com.ds2016;

import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.stream.SinkAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zwliew on 19/6/16.
 */
class GraphAlgo extends SinkAdapter implements DynamicAlgorithm {

    private static final int LOAD_HIST_SIZE = 3;
    private static int NODE_SPEED; /* What is the node processing speed? */
    private static final int LOAD_HIGH = NODE_SPEED;
    private static final int LOAD_MID = NODE_SPEED / 2;
    private static final int LOAD_LOW = NODE_SPEED / 3;

    private Graph mGraph;
    private HashMap<Edge, Integer> mEdgeLoads = new HashMap<>();
    private int mLoads[] = new int[LOAD_HIST_SIZE];

    @Override
    public void terminate() {
        mGraph.removeSink(this);
    }

    @Override
    public void init(Graph graph) {
        mGraph = graph;
        for (int i = 0; i < mLoads.length; i++) mLoads[i] = 0;
    }

    @Override
    public void compute() {
        for (int i = 0; i < getEdgeStatus().size() - 1; i++) {
            mEdgeLoads.put(mGraph.getEdge(i), getEdgeStatus().get(i));
            setEdgeColor(mGraph.getEdge(i));
        }
    }

    private void setEdgeColor(org.graphstream.graph.Edge edge) {
        int curLoad = mEdgeLoads.get(edge);
        int avgLoad = calcLoadAvg(curLoad);

        String loadLv;
        if (avgLoad > LOAD_MID) {
            loadLv = "highLoad";
        } else if (avgLoad > LOAD_LOW) {
            loadLv = "midLoad";
        } else {
            loadLv = "lowLoad";
        }
        edge.setAttribute("ui.class", loadLv);

    }

    private int calcLoadAvg(int curLoad) {
        int totalLoad = 0;
        for (int i = 0; i < mLoads.length - 1; i++) {
            mLoads[i] = mLoads[i + 1];
            totalLoad += mLoads[i];
        }
        mLoads[mLoads.length - 1] = curLoad;
        totalLoad += mLoads[mLoads.length - 1];
        return totalLoad / mLoads.length;
    }

    /* Duplicate */
    private ArrayList<Integer> getEdgeStatus() {
        final ArrayList<Edge_ACO> edgeList = new ArrayList<>();
        ArrayList<Integer> ret = new ArrayList<>();
        for (int a = 0; a < edgeList.size() - 1; a += 2) { // They come in pairs
            ret.add(edgeList.get(a).packets.size() + edgeList.get(a + 1).packets.size());
        }
        return ret;
    }
}

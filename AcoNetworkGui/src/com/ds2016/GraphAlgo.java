package com.ds2016;

import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.stream.SinkAdapter;

import java.util.ArrayList;

import static com.ds2016.Main.sAlgo;
import static com.ds2016.Main.sGraph;

/**
 * Created by zwliew on 19/6/16.
 * TODO: Add load averaging
 */
class GraphAlgo extends SinkAdapter implements DynamicAlgorithm {

    private static final double HIGH_LOAD_FACTOR = 3;
    private static final double MED_LOAD_FACTOR = 1.5;
    private static final double LOW_LOAD_FACTOR = 0.5;

    private int mLoadMean;

    @Override
    public void terminate() {
        sGraph.removeSink(this);
    }

    @Override
    public void init(Graph graph) {
    }

    @Override
    public void compute() {
        int temp = 0;
        ArrayList<Integer> edgeLoadList = sAlgo.getEdgeStatus();
        for (int edgeLoad : edgeLoadList) {
            temp += edgeLoad;
        }
        int loadTotal = temp;
        if (Main.DEBUG) System.out.println("compute: loadTotal = " + loadTotal);
        int edgeCount = edgeLoadList.size();
        if (Main.DEBUG) System.out.println("compute: edgeCount = " + edgeCount);
        mLoadMean = loadTotal / edgeCount;

        for (int i = 0; i < edgeCount; i++) {
            Edge edge = sGraph.getEdge(i);
            if (Main.DEBUG) System.out.println("compute: edgeId = " + edge.getId());
            setEdgeColor(edge, edgeLoadList.get(i));
        }
    }

    private void setEdgeColor(Edge edge, int curLoad) {
        //int avgLoad = calcLoadAvg(curLoad);
        if (Main.DEBUG) System.out.println("setEdgeColor: curLoad = " + curLoad);

        String loadLv;
        if (curLoad >= HIGH_LOAD_FACTOR * mLoadMean) {
            loadLv = "highLoad";
        } else if (curLoad >= MED_LOAD_FACTOR * mLoadMean) {
            loadLv = "midLoad";
        } else if (curLoad >= LOW_LOAD_FACTOR * mLoadMean) {
            loadLv = "lowLoad";
        } else {
            loadLv = "noLoad";
        }

        edge.setAttribute("ui.class", loadLv);
    }
}
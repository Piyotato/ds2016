package com.ds2016.ui;

import com.ds2016.Link;
import com.ds2016.Main;
import com.ds2016.listeners.GraphEventListener;
import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.stream.SinkAdapter;

import java.util.ArrayList;

/**
 * Created by zwliew on 19/6/16.
 * TODO: Add load averaging
 */
class GraphAlgo extends SinkAdapter implements DynamicAlgorithm {

    private static final double HIGH_LOAD_FACTOR = 3;
    private static final double MED_LOAD_FACTOR = 1.5;
    private static final double LOW_LOAD_FACTOR = 0.5;

    private GraphEventListener mListener;

    private int mLoadMean;

    GraphAlgo(final GraphEventListener listener) {
        mListener = listener;
    }

    @Override
    public void terminate() {
        mListener.onGraphTerminated(this);
    }

    @Override
    public void init(Graph graph) {
    }

    @Override
    public void compute() {
        final Graph graph = mListener.onGraphUpdated();
        int temp = 0;
        ArrayList<Integer> edgeLoadList = Link.sAlgorithm.getEdgeStatus();
        for (int edgeLoad : edgeLoadList) {
            temp += edgeLoad;
        }
        int loadTotal = temp;
        if (Main.DEBUG) System.out.println("compute: loadTotal = " + loadTotal);
        int edgeCount = edgeLoadList.size();
        if (Main.DEBUG) System.out.println("compute: edgeCount = " + edgeCount);
        mLoadMean = loadTotal / edgeCount;

        for (int i = 0; i < edgeCount; i++) {
            Edge edge = graph.getEdge(i);
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
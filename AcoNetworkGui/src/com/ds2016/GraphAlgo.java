package com.ds2016;

import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.stream.SinkAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ds2016.Main.sAlgo;
import static com.ds2016.Main.sGraph;

/**
 * Created by zwliew on 19/6/16.
 */
class GraphAlgo extends SinkAdapter implements DynamicAlgorithm {

    private static final int LOAD_HIST_SIZE = 3;
    private static final double HIGH_LOAD_FACTOR = 0.7;
    private static final double MED_LOAD_FACTOR = 0.3;

    private HashMap<Edge, Integer> mEdgeLoads = new HashMap<>();
    private int mLoads[] = new int[LOAD_HIST_SIZE];
    private int mTotalLoad;

    @Override
    public void terminate() {
        sGraph.removeSink(this);
    }

    @Override
    public void init(Graph graph) {
        for (int i = 0; i < mLoads.length; i++) {
            mLoads[i] = 0;
        }
    }

    @Override
    public void compute() {
        ArrayList<Integer> edgeLoadList = sAlgo.getEdgeStatus();
        int temp = 0;
        for (int edgeLoad : edgeLoadList) {
            temp += edgeLoad;
        }
        mTotalLoad = temp;

        for (int i = 0; i < sAlgo.getEdgeStatus().size() - 1; i++) {
            mEdgeLoads.put(sGraph.getEdge(i), sAlgo.getEdgeStatus().get(i));
            setEdgeColor(sGraph.getEdge(i));
        }
    }

    private void setEdgeColor(Edge edge) {
        int curLoad = mEdgeLoads.get(edge);
        int avgLoad = calcLoadAvg(curLoad);

        String loadLv;
        if (avgLoad > HIGH_LOAD_FACTOR * mTotalLoad) {
            loadLv = "highLoad";
        } else if (avgLoad > MED_LOAD_FACTOR * mTotalLoad) {
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
}

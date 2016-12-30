package com.ds2016.ui;

import com.ds2016.Link;
import com.ds2016.Main;
import com.ds2016.listeners.GraphEventListener;
import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.SinkAdapter;

import java.util.ArrayList;

/**
 * Created by zwliew on 19/6/16.
 * TODO: Add load averaging
 */
class GraphAlgo extends SinkAdapter implements DynamicAlgorithm {


    private GraphEventListener mListener;

    private int mEdgeLoadMean;
    private int mNodeLoadMean;

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
        final ArrayList<Integer> edgeLoadList = Link.sAlgorithm.getEdgeStatus();
        for (int edgeLoad : edgeLoadList) {
            temp += edgeLoad;
        }
        int loadTotal = temp;
        int edgeCount = edgeLoadList.size();
        mEdgeLoadMean = loadTotal / edgeCount;

        for (int i = 0; i < edgeCount; i++) {
            Edge edge = graph.getEdge(i);
            setEdgeColor(edge, edgeLoadList.get(i));
        }

        temp = 0;
        final ArrayList<Integer> nodeLoadlist = Link.sAlgorithm.getNodeStatus();
        for (int nodeLoad : nodeLoadlist) {
            temp += nodeLoad;
        }
        loadTotal = temp;
        final int nodeCount = nodeLoadlist.size();
        mNodeLoadMean = loadTotal / nodeCount;

        for (int i = 0; i < nodeCount; i++) {
            Node node = graph.getNode(i);
            if ("destination".equals(node.getAttribute("ui.class")) ||
                    "source".equals(node.getAttribute("ui.class"))) continue;
            setNodeColor(node, nodeLoadlist.get(i));
        }
    }

    private void setEdgeColor(final Edge edge, final int curLoad) {
        String loadLv;
        if (curLoad >= Main.HIGH_LOAD_FACTOR * mEdgeLoadMean) {
            loadLv = "highLoad";
        } else if (curLoad >= Main.MED_LOAD_FACTOR * mEdgeLoadMean) {
            loadLv = "midLoad";
        } else if (curLoad >= Main.LOW_LOAD_FACTOR * mEdgeLoadMean) {
            loadLv = "lowLoad";
        } else if (curLoad > 0) {
            loadLv = "minimalLoad";
        } else {
            loadLv = "noLoad";
        }

        edge.setAttribute("ui.class", loadLv);
    }

    private void setNodeColor(final Node node, final int curLoad) {
        String loadLv;
        if (mNodeLoadMean == 0) {
            loadLv = "minimalLoad";
        } else if (curLoad >= Main.HIGH_LOAD_FACTOR * mNodeLoadMean) {
            loadLv = "highLoad";
        } else if (curLoad >= Main.MED_LOAD_FACTOR * mNodeLoadMean) {
            loadLv = "midLoad";
        } else if (curLoad >= Main.LOW_LOAD_FACTOR * mNodeLoadMean) {
            loadLv = "lowLoad";
        } else if (curLoad > 0) {
            loadLv = "minimalLoad";
        } else {
            loadLv = "noLoad";
        }

        node.setAttribute("ui.class", loadLv);
    }
}
package com.ds2016;

import static com.ds2016.Main.*;
import static com.ds2016.NewGui.sGraphAlgo;

/**
 * Created by zwliew on 4/7/16.
 * <p>
 * Common methods which act on both the graph and the GUI
 */
class Link {

    static void tick() {
        sAlgo.tick();
        sGraphAlgo.compute();
    }

    static void addNode(int speed) {
        sGui.addNode(speed);
        sAlgo.addNode(speed);
    }

    static void toggleNode(int ID) {
        sGui.toggleNode(ID);
        sAlgo.toggleNode(ID);
    }

    static void addEdge(int node1, int node2, int cost) {
        sGui.addEdge(node1, node2, cost);
        sAlgo.addEdge(node1, node2, cost);
    }

    static void toggleEdge(int ID) {
        sGui.toggleEdge(ID);
        sAlgo.toggleEdge(ID);
    }

    static void start() {
        sGui.startThread();
        Main.startThread();
    }

    static void stop() {
        sGui.stopThread();
        Main.stopThread();
    }

    static void update() {
        stop();
        sGui.update();
        sAlgo.build(sGui.mNodeList, sGui.mEdgeList, sParams.getSource(), sParams.getDestination());

    }

    static void setAlgorithm(int algo) {
        stop();
        sParams.setAlgorithm(algo);
        sAlgo.build(sGui.mNodeList, sGui.mEdgeList, sParams.getSource(), sParams.getDestination());
    }
}

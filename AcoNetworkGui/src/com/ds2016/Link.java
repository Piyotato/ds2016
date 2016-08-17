package com.ds2016;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import static com.ds2016.Main.*;
import static com.ds2016.NewGui.sDataChart;
import static com.ds2016.NewGui.sGraphAlgo;

/**
 * Created by zwliew on 4/7/16.
 * <p>
 * Common methods which act on both the graph and the GUI
 */
class Link {

    static void tick() {
        sTickVal = sAlgo.tick();
        sGraphAlgo.compute();
        sDataChart.updateCharts();
    }

    static void addNode(int speed) {
        Mutex mutex = new Mutex();
        try {
            mutex.acquire();
            try {
                sGui.addNode(speed);
                sAlgo.addNode(speed);
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void toggleNode(int ID) {
        Mutex mutex = new Mutex();
        try {
            mutex.acquire();
            try {
                sGui.toggleNode(ID);
                sAlgo.toggleNode(ID);
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void addEdge(int node1, int node2, int cost) {
        Mutex mutex = new Mutex();
        try {
            mutex.acquire();
            try {
                sGui.addEdge(node1, node2, cost);
                sAlgo.addEdge(node1, node2, cost);
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void toggleEdge(int ID) {
        Mutex mutex = new Mutex();
        try {
            mutex.acquire();
            try {
                sGui.toggleEdge(ID);
                sAlgo.toggleEdge(ID);
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void start() {
        sGui.startThread();
        Main.startThread();
    }

    static void stop() {
        sGui.stopThread();
        Main.stopThread();
        System.out.println("Throughput: " + sTotalThroughput + " Success: " + sTotalSuccess);
    }

    static void update() {
        sGui.update();
        Main.initAlgo();
        sAlgo.build(sGui.mNodeList, sGui.mEdgeList, sParams.getSource(), sParams.getDestination());
        sDataChart.resetCharts();
    }

    /**
     * OSPF chokes; others ok
     */
    static void buildDiamond1() {
        // Add 4 nodes
        addNode(1000);
        for (int i = 0; i < 2; i++) {
            addNode(600);
        }
        addNode(800);

        addEdge(0, 1, 4);
        addEdge(1, 2, 4);
        addEdge(0, 3, 4);
        addEdge(3, 2, 4);
    }

    static void buildDoubleDiamond() {
        // Add 7 nodes
        addNode(1000);
        addNode(600);
        addNode(600);
        addNode(1000);
        addNode(600);
        addNode(600);
        addNode(800);

        addEdge(0, 1, 4);
        addEdge(0, 2, 4);
        addEdge(1, 3, 4);
        addEdge(2, 3, 4);
        addEdge(6, 4, 4);
        addEdge(6, 5, 4);
        addEdge(4, 3, 4);
        addEdge(5, 3, 4);
    }

    static void buildBananaTree() {
        // Add 31 nodes
        addNode(1000);
        for (int i = 0; i < 29; i++) {
            addNode(600);
        }
        addNode(800);

        addEdge(0, 1, 4);
        addEdge(0, 2, 4);
        addEdge(0, 3, 4);
        addEdge(0, 4, 4);
        addEdge(0, 5, 4);

        addEdge(6, 1, 4);
        addEdge(6, 7, 4);
        addEdge(6, 8, 4);
        addEdge(6, 9, 4);
        addEdge(6, 10, 4);

        addEdge(11, 2, 4);
        addEdge(11, 12, 4);
        addEdge(11, 13, 4);
        addEdge(11, 14, 4);
        addEdge(11, 15, 4);

        addEdge(16, 3, 4);
        addEdge(16, 17, 4);
        addEdge(16, 18, 4);
        addEdge(16, 19, 4);
        addEdge(16, 20, 4);

        addEdge(21, 4, 4);
        addEdge(21, 22, 4);
        addEdge(21, 23, 4);
        addEdge(21, 24, 4);
        addEdge(21, 25, 4);

        addEdge(26, 5, 4);
        addEdge(26, 27, 4);
        addEdge(26, 28, 4);
        addEdge(26, 29, 4);
        addEdge(26, 30, 4);
    }
}

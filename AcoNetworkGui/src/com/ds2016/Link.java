package com.ds2016;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import static com.ds2016.Gui.sDataChart;
import static com.ds2016.Gui.sGraphAlgo;
import static com.ds2016.Main.*;

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

    static void addNode() {
        Mutex mutex = new Mutex();
        try {
            mutex.acquire();
            try {
                sGui.addNode();
                sAlgo.addNode();
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

    static void addEdge(int node1, int node2, int cost, int bandwidth) {
        Mutex mutex = new Mutex();
        try {
            mutex.acquire();
            try {
                sGui.addEdge(node1, node2, cost, bandwidth);
                sAlgo.addEdge(node1, node2, cost, bandwidth);
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
    }

    static void update() {
        sGui.update();
        Main.initAlgo();
        sAlgo.build(sGui.mNodeList, sGui.mEdgeList, sParams.getSource(), sParams.getDestination());
        sDataChart.resetCharts();
    }

    /*
        // OSPF chokes; others ok
        static void buildDiamond() {
            // Add 4 nodes
            addNode(1000);
            addNode(600);
            addNode(800);
            addNode(600);

            addEdge(0, 1, 4);
            addEdge(1, 2, 6);
            addEdge(2, 3, 6);
            addEdge(3, 0, 5);
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
            addEdge(0, 2, 6);
            addEdge(1, 3, 4);
            addEdge(2, 3, 4);
            addEdge(3, 4, 4);
            addEdge(3, 5, 4);
            addEdge(4, 6, 4);
            addEdge(5, 6, 4);
        }

        static void buildBananaTree() {
            // Add 31 nodes
            addNode(1000);
            addNode(800);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(800);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);
            addNode(600);

            addEdge(0, 1, 4);
            addEdge(0, 2, 4);
            addEdge(0, 3, 4);
            addEdge(0, 4, 4);
            addEdge(0, 5, 4);

            addEdge(1, 6, 4);
            addEdge(6, 7, 4);
            addEdge(6, 8, 4);
            addEdge(6, 9, 4);
            addEdge(6, 10, 4);

            addEdge(2, 11, 4);
            addEdge(11, 12, 4);
            addEdge(11, 13, 4);
            addEdge(11, 14, 4);
            addEdge(11, 15, 4);

            addEdge(3, 16, 4);
            addEdge(16, 17, 4);
            addEdge(16, 18, 4);
            addEdge(16, 19, 4);
            addEdge(16, 20, 4);

            addEdge(4, 21, 4);
            addEdge(21, 22, 4);
            addEdge(21, 23, 4);
            addEdge(21, 24, 4);
            addEdge(21, 25, 4);

            addEdge(5, 26, 4);
            addEdge(26, 27, 4);
            addEdge(26, 28, 4);
            addEdge(26, 29, 4);
            addEdge(26, 30, 4);
        }
    */
    static void buildNsfNet() {
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();

        addEdge(0, 1, 9, 1500);
        addEdge(0, 2, 9, 1500);
        addEdge(0, 3, 7, 1500);

        addEdge(1, 3, 13, 1500);
        addEdge(1, 6, 20, 1500);

        addEdge(2, 4, 70, 1500);
        addEdge(2, 7, 16, 1500);

        addEdge(3, 10, 15, 1500);

        addEdge(4, 5, 7, 1500);
        addEdge(4, 10, 11, 1500);

        addEdge(5, 6, 7, 1500);

        addEdge(6, 9, 7, 1500);

        addEdge(7, 8, 5, 1500);
        addEdge(7, 13, 8, 1500);

        addEdge(8, 9, 5, 1500);
        addEdge(8, 12, 7, 1500);

        addEdge(9, 11, 8, 1500);
        addEdge(9, 13, 8, 1500);

        addEdge(10, 11, 9, 1500);
        addEdge(10, 12, 14, 1500);

        addEdge(12, 13, 4, 1500);
    }

    static void buildNttNet() {
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();
        addNode();

        addEdge(0, 2, 18, 6000);

        addEdge(1, 2, 15, 6000);

        addEdge(2, 5, 18, 6000);
        addEdge(2, 7, 20, 6000);

        addEdge(3, 5, 12, 6000);

        addEdge(4, 7, 21, 6000);

        addEdge(5, 6, 12, 6000);
        addEdge(5, 11, 40, 6000);

        addEdge(6, 7, 15, 6000);
        addEdge(6, 8, 17, 6000);

        addEdge(7, 10, 23, 6000);

        addEdge(8, 9, 22, 6000);

        addEdge(10, 12, 20, 6000);

        addEdge(11, 14, 17, 6000);

        addEdge(12, 15, 10, 6000);

        addEdge(13, 15, 12, 6000);

        addEdge(14, 19, 20, 6000);

        addEdge(15, 16, 30, 6000);

        addEdge(16, 20, 14, 6000);
        addEdge(16, 23, 15, 6000);

        addEdge(17, 18, 21, 6000);

        addEdge(18, 20, 10, 6000);

        addEdge(19, 20, 12, 6000);

        addEdge(20, 21, 10, 6000);
        addEdge(20, 23, 13, 6000);

        addEdge(21, 22, 11, 6000);
        addEdge(21, 24, 11, 6000);

        addEdge(22, 26, 16, 6000);

        addEdge(23, 25, 13, 6000);

        addEdge(24, 29, 11, 6000);

        addEdge(25, 28, 11, 6000);
        addEdge(25, 31, 10, 6000);

        addEdge(26, 27, 11, 6000);

        addEdge(27, 35, 15, 6000);

        addEdge(28, 29, 11, 6000);
        addEdge(28, 30, 11, 6000);

        addEdge(29, 30, 12, 6000);

        addEdge(30, 33, 12, 6000);

        addEdge(31, 32, 10, 6000);

        addEdge(33, 34, 11, 6000);
        addEdge(33, 36, 16, 6000);

        addEdge(34, 35, 12, 6000);
        addEdge(34, 38, 17, 6000);

        addEdge(35, 42, 23, 6000);

        addEdge(36, 37, 11, 6000);

        addEdge(37, 40, 12, 6000);

        addEdge(38, 39, 10, 6000);

        addEdge(39, 40, 11, 6000);
        addEdge(39, 41, 16, 6000);

        addEdge(40, 41, 19, 6000);

        addEdge(41, 42, 23, 6000);
        addEdge(41, 45, 20, 6000);

        addEdge(42, 43, 20, 6000);
        addEdge(42, 51, 50, 6000);

        addEdge(43, 44, 12, 6000);
        addEdge(43, 46, 12, 6000);

        addEdge(44, 45, 11, 6000);

        addEdge(45, 48, 27, 6000);

        addEdge(46, 47, 21, 6000);

        addEdge(47, 48, 11, 6000);
        addEdge(47, 49, 22, 6000);

        addEdge(48, 50, 28, 6000);

        addEdge(49, 50, 16, 6000);
        addEdge(49, 51, 21, 6000);

        addEdge(50, 51, 12, 6000);
        addEdge(50, 53, 27, 6000);

        addEdge(51, 52, 17, 6000);

        addEdge(52, 54, 19, 6000);

        addEdge(53, 54, 13, 6000);
    }
}

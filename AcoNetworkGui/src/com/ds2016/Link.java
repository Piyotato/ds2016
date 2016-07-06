package com.ds2016;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

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
    }

    static void update() {
        sGui.update();
        Main.initAlgo();
        sAlgo.build(sGui.mNodeList, sGui.mEdgeList, sParams.getSource(), sParams.getDestination());

    }

    static void setAlgorithm(int algo) {
        stop();
        sParams.setAlgorithm(algo);
        sAlgo.build(sGui.mNodeList, sGui.mEdgeList, sParams.getSource(), sParams.getDestination());
    }

    /**
     * Construct a simple diamond graph
     * for demonstration purposes
     */
    static void buildDemoGraph1() {
        addNode(80); // Node 0
        addNode(40); // Node 1
        addNode(80); // Node 2
        addNode(40); // Node 3

        addEdge(0, 1, 40);
        addEdge(1, 2, 40);
        addEdge(0, 3, 40);
        addEdge(3, 2, 40);
    }
}

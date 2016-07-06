package com.ds2016;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;
import org.graphstream.graph.Graph;

/**
 * Created by zwliew on 13/6/16.
 */
public class Main {

    static final boolean DEBUG = true;

    private static final String ALGO_THREAD = "ALGO_THREAD";
    private static final int POLL_MS = 1000;
    private static final int TTL = 15; // Used by ACO
    private static final int INTERVAL = 1; // Used by ACO

    static ParameterStorage sParams;
    static AlgorithmBase sAlgo;
    static Graph sGraph;
    static NewGui sGui;

    private static Thread mThread;
    private static Runnable mRunnable;

    public static void main(String[] args) {
        sParams = new ParameterStorage(0.4, 15, 0, 2, ParameterStorage.ALGO_OSPF);
        sGui = new NewGui();
        sAlgo = new OSPF(TTL);

        sGui.init();

        // Build demo graph
        Link.buildDemoGraph1();

        Mutex mutex = new Mutex();

        mRunnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    mutex.acquire();
                    try {
                        sAlgo.tick();
                    } finally {
                        mutex.release();
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // This exception is expected, swallow it.
                    //e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        };
    }

    static void startThread() {
        mThread = new Thread(mRunnable, ALGO_THREAD);
        mThread.start();
    }

    static void stopThread() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    static void updateAlgo() {
        // Start a new algorithm
        int algo = sParams.getAlgorithm();
        if (DEBUG) System.out.println("updateAlgo: algo = " + algo);
        switch (algo) {
            case ParameterStorage.ALGO_OSPF:
                sAlgo = new OSPF(TTL);
                break;
            case ParameterStorage.ALGO_ANTNET:
                sAlgo = new AntNet(sParams.getAlpha(), TTL, sParams.getInterval());
                break;
            case ParameterStorage.ALGO_EACO:
                sAlgo = new EACO(sParams.getAlpha(), TTL, sParams.getInterval());
                break;
            default:
                break;
        }
        sAlgo.build(sGui.mNodeList, sGui.mEdgeList, sParams.getSource(), sParams.getDestination());
    }
}

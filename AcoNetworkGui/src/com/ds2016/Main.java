package com.ds2016;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;
import javafx.util.Pair;
import org.graphstream.graph.Graph;

/**
 * Created by zwliew on 13/6/16.
 */
public class Main {

    static final boolean DEBUG = false;
    static final int POLL_MS = 5; // Algorithm tick delay in ms
    private static final String ALGO_THREAD = "ALGO_THREAD";
    private static final int TTL_MS = 15000 / POLL_MS; // Time to live of ants in ms, relative to POLL_MS
    static ParameterStorage sParams;
    static AlgorithmBase sAlgo;
    static Graph sGraph;
    static NewGui sGui;
    static Pair<Integer, Integer> sTickVal;
    private static Thread mThread;
    private static Runnable mRunnable;

    public static void main(String[] args) {
        sParams = new ParameterStorage(0.4, 1000, 0.3, 0, 2, ParameterStorage.ALGO_OSPF);
        sGui = new NewGui();
        initAlgo();

        sGui.init();

        Link.buildDoubleDiamond();

        Mutex mutex = new Mutex();

        mRunnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    mutex.acquire();
                    try {
                        sTickVal = sAlgo.tick();
                    } finally {
                        mutex.release();
                    }
                    Thread.sleep(POLL_MS);
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

    static void initAlgo() {
        // Start a new algorithm
        int algo = sParams.getAlgorithm();
        if (DEBUG) System.out.println("updateAlgo: algo = " + algo);
        switch (algo) {
            case ParameterStorage.ALGO_OSPF:
                sAlgo = new OSPF(TTL_MS, sParams.getTraffic());
                break;
            case ParameterStorage.ALGO_ANTNET:
                sAlgo = new AntNet(sParams.getAlpha(), sParams.getTraffic(), TTL_MS, sParams.getInterval());
                break;
            case ParameterStorage.ALGO_EACO:
                sAlgo = new EACO(sParams.getAlpha(), sParams.getTraffic(), TTL_MS, sParams.getInterval());
                break;
            default:
                break;
        }
    }
}

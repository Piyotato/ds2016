package com.ds2016;

import org.graphstream.graph.Graph;

/**
 * Created by zwliew on 13/6/16.
 */
public class Main {
    private static final String ALGO_THREAD = "ALGO_THREAD";

    static ParameterStorage mParams = new ParameterStorage(1, 1, 1, 1, 1, 1, 2);

    static AlgorithmBase mAlgo = new EACO(1, 1, 1, 1, 1, 1, 2);
    static Graph mGraph;

    private static Thread mThread;
    private static Runnable mRunnable;

    public static void main(String[] args) {
        new NewGui();
        NewGui.main();

        mRunnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    /* Algo stuff */

                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        };
        mThread = new Thread(mRunnable, ALGO_THREAD);
    }
}

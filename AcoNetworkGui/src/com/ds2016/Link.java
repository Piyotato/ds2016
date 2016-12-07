package com.ds2016;

import com.ds2016.listeners.GuiEventListener;
import com.ds2016.ui.Gui;
import com.ds2016.ui.ParameterStorage;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

/**
 * Created by zwliew on 4/7/16.
 * <p>
 * Common methods which act on both the graph and the GUI
 */
public class Link implements GuiEventListener {

    private static final int ALGO_OSPF = 0;
    private static final int ALGO_ANTNET = 1;
    private static final int ALGO_EACO = 2;
    private static final int POLL_MS = 5;
    private static final int TTL_MS = 150;
    public static AlgorithmBase sAlgorithm; // TODO
    public static long sThroughput; // TODO
    private final Mutex mMutex = new Mutex();
    private Gui mGui;
    private ParameterStorage mParams;
    private Thread mThread;
    private Runnable mRunnable;
    private int mNumTicks;

    Link() {
        mGui = new Gui(this);
        mParams = new ParameterStorage(0, 6,
                0.4, 0.3, 1000, 6000, ALGO_EACO);
        sAlgorithm = new EACO(0.4, 1000, TTL_MS, 0.3);
        mNumTicks = 0;
    }

    void init() {
        mGui.init();

        mRunnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    mMutex.acquire();
                    try {
                        tick();
                        mGui.tick();
                        mNumTicks++;
                        if (mNumTicks == mParams.getNumTicks()) {
                            mNumTicks = 0;
                            stop();
                        }
                    } finally {
                        mMutex.release();
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

    private void tick() {
        sThroughput = sAlgorithm.tick();
    }

    private void addNode() {
        try {
            mMutex.acquire();
            try {
                sAlgorithm.addNode();
            } finally {
                mMutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void toggleNode(final int id) {
        try {
            mMutex.acquire();
            try {
                sAlgorithm.toggleNode(id);
            } finally {
                mMutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addEdge(final int source,
                         final int destination,
                         final int cost,
                         final int bandwidth) {
        try {
            mMutex.acquire();
            try {
                sAlgorithm.addEdge(source, destination, cost, bandwidth);
            } finally {
                mMutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void toggleEdge(final int id) {
        try {
            mMutex.acquire();
            try {
                sAlgorithm.toggleEdge(id);
            } finally {
                mMutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        mThread = new Thread(mRunnable, "ALGO_THREAD");
        mThread.start();
    }

    private void stop() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    private void update(final ParameterStorage params) {
        mParams.setSource(params.getSource());
        mParams.setDestination(params.getDestination());
        mParams.setAlpha(params.getAlpha());
        mParams.setInterval(params.getInterval());
        mParams.setTraffic(params.getTraffic());
        mParams.setNumTicks(params.getNumTicks());

        buildNewAlgorithm(params);
    }

    private void buildNewAlgorithm(final ParameterStorage params) {
        final int source = params.getSource();
        final int destination = params.getDestination();
        final double alpha = params.getAlpha();
        final double interval = params.getInterval();
        final int traffic = params.getTraffic();
        final int algorithm = mParams.getAlgorithm();

        switch (algorithm) {
            case ALGO_OSPF:
                sAlgorithm = new OSPF(TTL_MS, traffic);
                break;
            case ALGO_ANTNET:
                sAlgorithm = new AntNet(alpha, traffic, TTL_MS, interval);
                break;
            case ALGO_EACO:
                sAlgorithm = new EACO(alpha, traffic, TTL_MS, interval);
                break;
        }
        sAlgorithm.build(mGui.mNodeList, mGui.mEdgeList, source, destination);
        if (Main.DEBUG) System.out.println("buildNewAlgorithm: algorithm = " + algorithm);
    }

    @Override
    public void onStart() {
        start();
    }

    @Override
    public void onStop() {
        stop();
    }

    @Override
    public void onTick() {
        tick();
    }

    @Override
    public void onUpdate(final ParameterStorage params) {
        update(params);
    }

    @Override
    public void onAlgorithmChanged(final int algorithmId) {
        mParams.setAlgorithm(algorithmId);
    }

    @Override
    public void onNodeAdded() {
        addNode();
    }

    @Override
    public void onNodeToggled(final int nodeId) {
        toggleNode(nodeId);
    }

    @Override
    public void onEdgeAdded(
            final int source, final int destination, final int cost, final int bandwidth) {
        addEdge(source, destination, cost, bandwidth);
    }

    @Override
    public void onEdgeToggled(final int edgeId) {
        toggleEdge(edgeId);
    }
}

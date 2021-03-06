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
    public static AlgorithmBase sAlgorithm; // TODO
    public static int sThroughput; // TODO
    private final Mutex mMutex = new Mutex();
    private Gui mGui;
    private ParameterStorage mParams;
    private Thread mThread;
    private Runnable mRunnable;
    private boolean mStarted;

    Link() {
        mGui = new Gui(this);
        mParams = new ParameterStorage(0, 6,
                0.4, 0.1, 1, 30000, ALGO_EACO);
        sAlgorithm = new EACO(mParams.getAlpha(), mParams.getTraffic(), Main.TTL_MS, mParams.getInterval());
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
                        if (sAlgorithm.getCurrentTime() >= mParams.getNumTicks()
                                && mParams.getNumTicks() > 0) {
                            stop();
                        }
                    } finally {
                        mMutex.release();
                    }
                    Thread.sleep(1);
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
        if (mStarted) return;
        mThread = new Thread(mRunnable, "ALGO_THREAD");
        mThread.start();
        mStarted = true;
    }

    private void stop() {
        if (!mStarted) return;
        if (mThread != null) {
            mThread.interrupt();
        }
        mStarted = false;
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
                sAlgorithm = new OSPF(Main.TTL_MS, traffic);
                break;
            case ALGO_ANTNET:
                sAlgorithm = new AntNet(alpha, traffic, Main.TTL_MS, interval);
                break;
            case ALGO_EACO:
                sAlgorithm = new EACO(alpha, traffic, Main.TTL_MS, interval);
                break;
        }
        sAlgorithm.build(mGui.mNodeList, mGui.mEdgeList, source, destination);
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

package com.ds2016;

import static com.ds2016.NewGui.sDataChart;
import static com.ds2016.NewGui.sGraphAlgo;

/**
 * Created by zwliew on 20/6/16.
 */
class GraphRunnable implements Runnable {
    private int mNumTicks = 0;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            sGraphAlgo.compute();
            sDataChart.updateCharts();
            mNumTicks++;
            if (mNumTicks >= Main.NUM_TICKS) {
                Thread.currentThread().interrupt();
            }
            try {
                Thread.sleep(Main.POLL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

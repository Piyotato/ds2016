package com.ds2016;

import static com.ds2016.NewGui.sDataChart;
import static com.ds2016.NewGui.sGraphAlgo;

/**
 * Created by zwliew on 20/6/16.
 */
class GraphRunnable implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            sGraphAlgo.compute();
            sDataChart.updateCharts();
            try {
                Thread.sleep(Main.POLL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

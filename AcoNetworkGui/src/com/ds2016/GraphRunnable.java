package com.ds2016;

import static com.ds2016.NewGui.sGraphAlgo;

/**
 * Created by zwliew on 20/6/16.
 */
class GraphRunnable implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            sGraphAlgo.compute();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

package com.ds2016.listeners;

/**
 * Created by wchee on 24/11/2016.
 */
public interface NetworkEventListener {
    void onNodeAdded();

    void onEdgeAdded(final int source, final int destination, final int cost, final int bandwidth);
}

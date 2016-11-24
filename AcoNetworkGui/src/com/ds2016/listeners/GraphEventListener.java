package com.ds2016.listeners;

import org.graphstream.graph.Graph;
import org.graphstream.stream.Sink;

/**
 * Created by wchee on 24/11/2016.
 */
public interface GraphEventListener {
    void onGraphTerminated(final Sink sink);

    Graph onGraphUpdated();
}

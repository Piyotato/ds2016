package com.ds2016.listeners;

import com.ds2016.ui.ParameterStorage;

/**
 * Created by wchee on 24/11/2016.
 */
public interface GuiEventListener {
    void onStart();

    void onStop();

    void onTick();

    void onUpdate(final ParameterStorage params);

    void onAlgorithmChanged(final int algorithmId);

    void onNodeAdded();

    void onNodeToggled(final int nodeId);

    void onEdgeAdded(final int source, final int destination, final int cost, final int bandwidth);

    void onEdgeToggled(final int edgeId);
}

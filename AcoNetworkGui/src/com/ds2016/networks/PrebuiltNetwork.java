package com.ds2016.networks;

import com.ds2016.listeners.NetworkEventListener;

/**
 * Created by wchee on 24/11/2016.
 */
public abstract class PrebuiltNetwork {
    NetworkEventListener mListener;

    public void init(final NetworkEventListener listener) {
        mListener = listener;
    }

    public abstract void build();
}

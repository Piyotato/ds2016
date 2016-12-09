package com.ds2016.networks;

/**
 * Created by wchee on 9/12/2016.
 */
public class SimpleNetwork extends Network {
    @Override
    public void build() {
        for (int i = 0; i < 8; i++) {
            mListener.onNodeAdded();
        }

        mListener.onEdgeAdded(0, 1, 1, 10000);
        mListener.onEdgeAdded(0, 2, 1, 10000);
        mListener.onEdgeAdded(0, 7, 1, 10000);

        mListener.onEdgeAdded(1, 3, 1, 10000);

        mListener.onEdgeAdded(2, 4, 1, 10000);

        mListener.onEdgeAdded(3, 4, 1, 10000);

        mListener.onEdgeAdded(4, 5, 1, 10000);

        mListener.onEdgeAdded(5, 6, 1, 10000);

        mListener.onEdgeAdded(6, 7, 1, 10000);
    }
}

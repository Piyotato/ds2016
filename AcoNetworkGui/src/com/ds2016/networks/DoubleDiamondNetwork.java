package com.ds2016.networks;

/**
 * Created by zwliew on 7/12/16.
 */
public class DoubleDiamondNetwork extends Network {
    @Override
    public void build() {
        for (int i = 0; i < 7; i++) {
            mListener.onNodeAdded();
        }

        mListener.onEdgeAdded(0, 1, 4, 600);
        mListener.onEdgeAdded(0, 2, 6, 600);
        mListener.onEdgeAdded(1, 3, 4, 600);
        mListener.onEdgeAdded(2, 3, 4, 600);
        mListener.onEdgeAdded(3, 4, 4, 600);
        mListener.onEdgeAdded(3, 5, 4, 600);
        mListener.onEdgeAdded(4, 6, 4, 600);
        mListener.onEdgeAdded(5, 6, 4, 600);
    }
}

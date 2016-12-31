package com.ds2016.networks;

/**
 * Created by zwliew on 31/12/16.
 */
public class BananaTreeNetwork extends Network {
    @Override
    public void build() {
        for (int i = 0; i < 31; i++) {
            mListener.onNodeAdded();
        }

        mListener.onEdgeAdded(0, 1, 4, 2);
        mListener.onEdgeAdded(0, 2, 4, 2);
        mListener.onEdgeAdded(0, 3, 4, 2);
        mListener.onEdgeAdded(0, 4, 4, 2);
        mListener.onEdgeAdded(0, 5, 4, 2);

        mListener.onEdgeAdded(1, 6, 4, 2);
        mListener.onEdgeAdded(6, 7, 4, 2);
        mListener.onEdgeAdded(6, 8, 4, 2);
        mListener.onEdgeAdded(6, 9, 4, 2);
        mListener.onEdgeAdded(6, 10, 4, 2);

        mListener.onEdgeAdded(2, 11, 4, 2);
        mListener.onEdgeAdded(11, 12, 4, 2);
        mListener.onEdgeAdded(11, 13, 4, 2);
        mListener.onEdgeAdded(11, 14, 4, 2);
        mListener.onEdgeAdded(11, 15, 4, 2);

        mListener.onEdgeAdded(3, 16, 4, 2);
        mListener.onEdgeAdded(16, 17, 4, 2);
        mListener.onEdgeAdded(16, 18, 4, 2);
        mListener.onEdgeAdded(16, 19, 4, 2);
        mListener.onEdgeAdded(16, 20, 4, 2);

        mListener.onEdgeAdded(4, 21, 4, 2);
        mListener.onEdgeAdded(21, 22, 4, 2);
        mListener.onEdgeAdded(21, 23, 4, 2);
        mListener.onEdgeAdded(21, 24, 4, 2);
        mListener.onEdgeAdded(21, 25, 4, 2);

        mListener.onEdgeAdded(5, 26, 4, 2);
        mListener.onEdgeAdded(26, 27, 4, 2);
        mListener.onEdgeAdded(26, 28, 4, 2);
        mListener.onEdgeAdded(26, 29, 4, 2);
        mListener.onEdgeAdded(26, 30, 4, 2);
    }
}

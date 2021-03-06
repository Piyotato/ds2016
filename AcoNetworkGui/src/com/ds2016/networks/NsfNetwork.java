package com.ds2016.networks;

/**
 * Created by wchee on 24/11/2016.
 */
public class NsfNetwork extends Network {
    @Override
    public void build() {
        for (int i = 0; i < 14; i++) {
            mListener.onNodeAdded();
        }

        mListener.onEdgeAdded(0, 1, 9, 1);
        mListener.onEdgeAdded(0, 2, 9, 1);
        mListener.onEdgeAdded(0, 3, 7, 1);

        mListener.onEdgeAdded(1, 3, 13, 1);
        mListener.onEdgeAdded(1, 6, 20, 1);

        mListener.onEdgeAdded(2, 4, 70, 1);
        mListener.onEdgeAdded(2, 7, 16, 1);

        mListener.onEdgeAdded(3, 10, 15, 1);

        mListener.onEdgeAdded(4, 5, 7, 1);
        mListener.onEdgeAdded(4, 10, 11, 1);

        mListener.onEdgeAdded(5, 6, 7, 1);

        mListener.onEdgeAdded(6, 9, 7, 1);

        mListener.onEdgeAdded(7, 8, 5, 1);
        mListener.onEdgeAdded(7, 13, 8, 1);

        mListener.onEdgeAdded(8, 9, 5, 1);
        mListener.onEdgeAdded(8, 12, 7, 1);

        mListener.onEdgeAdded(9, 11, 8, 1);
        mListener.onEdgeAdded(9, 13, 8, 1);

        mListener.onEdgeAdded(10, 11, 9, 1);
        mListener.onEdgeAdded(10, 12, 14, 1);

        mListener.onEdgeAdded(12, 13, 4, 1);
    }
}

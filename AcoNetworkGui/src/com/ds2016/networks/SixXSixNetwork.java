package com.ds2016.networks;

/**
 * Created by wchee on 9/12/2016.
 */
public class SixXSixNetwork extends Network {
    @Override
    public void build() {
        for (int i = 0; i < 36; i++) {
            mListener.onNodeAdded();
        }

        mListener.onEdgeAdded(0, 1, 1, 10000);
        mListener.onEdgeAdded(0, 3, 1, 10000);

        mListener.onEdgeAdded(1, 2, 1, 10000);
        mListener.onEdgeAdded(1, 4, 1, 10000);

        mListener.onEdgeAdded(2, 5, 1, 10000);

        mListener.onEdgeAdded(3, 4, 1, 10000);
        mListener.onEdgeAdded(3, 6, 1, 10000);

        mListener.onEdgeAdded(4, 5, 1, 10000);
        mListener.onEdgeAdded(4, 7, 1, 10000);

        mListener.onEdgeAdded(5, 8, 1, 10000);

        mListener.onEdgeAdded(6, 7, 1, 10000);
        mListener.onEdgeAdded(6, 9, 1, 10000);

        mListener.onEdgeAdded(7, 8, 1, 10000);
        mListener.onEdgeAdded(7, 10, 1, 10000);

        mListener.onEdgeAdded(8, 11, 1, 10000);

        mListener.onEdgeAdded(9, 10, 1, 10000);
        mListener.onEdgeAdded(9, 12, 1, 10000);

        mListener.onEdgeAdded(10, 13, 1, 10000);
        mListener.onEdgeAdded(10, 11, 1, 10000);

        mListener.onEdgeAdded(11, 14, 1, 10000);
        mListener.onEdgeAdded(11, 29, 1, 10000);

        mListener.onEdgeAdded(12, 15, 1, 10000);

        mListener.onEdgeAdded(13, 14, 1, 10000);

        mListener.onEdgeAdded(15, 16, 1, 10000);

        mListener.onEdgeAdded(16, 17, 1, 10000);

        mListener.onEdgeAdded(17, 35, 1, 10000);

        mListener.onEdgeAdded(18, 19, 1, 10000);
        mListener.onEdgeAdded(18, 21, 1, 10000);

        mListener.onEdgeAdded(19, 20, 1, 10000);
        mListener.onEdgeAdded(19, 22, 1, 10000);

        mListener.onEdgeAdded(20, 23, 1, 10000);

        mListener.onEdgeAdded(21, 22, 1, 10000);
        mListener.onEdgeAdded(21, 24, 1, 10000);

        mListener.onEdgeAdded(22, 23, 1, 10000);
        mListener.onEdgeAdded(22, 25, 1, 10000);

        mListener.onEdgeAdded(23, 26, 1, 10000);

        mListener.onEdgeAdded(24, 25, 1, 10000);
        mListener.onEdgeAdded(24, 27, 1, 10000);

        mListener.onEdgeAdded(25, 26, 1, 10000);
        mListener.onEdgeAdded(25, 28, 1, 10000);

        mListener.onEdgeAdded(26, 29, 1, 10000);

        mListener.onEdgeAdded(27, 28, 1, 10000);
        mListener.onEdgeAdded(27, 30, 1, 10000);

        mListener.onEdgeAdded(28, 31, 1, 10000);
        mListener.onEdgeAdded(28, 29, 1, 10000);

        mListener.onEdgeAdded(29, 32, 1, 10000);

        mListener.onEdgeAdded(30, 33, 1, 10000);

        mListener.onEdgeAdded(31, 32, 1, 10000);

        mListener.onEdgeAdded(33, 34, 1, 10000);

        mListener.onEdgeAdded(34, 35, 1, 10000);
    }
}

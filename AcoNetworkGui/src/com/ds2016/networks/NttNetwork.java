package com.ds2016.networks;

/**
 * Created by wchee on 24/11/2016.
 */
public class NttNetwork extends Network {
    @Override
    public void build() {
        for (int i = 0; i < 55; i++) {
            mListener.onNodeAdded();
        }

        mListener.onEdgeAdded(0, 2, 18, 4);

        mListener.onEdgeAdded(1, 2, 15, 4);

        mListener.onEdgeAdded(2, 5, 18, 4);
        mListener.onEdgeAdded(2, 7, 20, 4);

        mListener.onEdgeAdded(3, 5, 12, 4);

        mListener.onEdgeAdded(4, 7, 21, 4);

        mListener.onEdgeAdded(5, 6, 12, 4);
        mListener.onEdgeAdded(5, 11, 40, 4);

        mListener.onEdgeAdded(6, 7, 15, 4);
        mListener.onEdgeAdded(6, 8, 17, 4);

        mListener.onEdgeAdded(7, 10, 23, 4);

        mListener.onEdgeAdded(8, 9, 22, 4);

        mListener.onEdgeAdded(10, 12, 20, 4);

        mListener.onEdgeAdded(11, 14, 17, 4);

        mListener.onEdgeAdded(12, 15, 10, 4);

        mListener.onEdgeAdded(13, 15, 12, 4);

        mListener.onEdgeAdded(14, 19, 20, 4);

        mListener.onEdgeAdded(15, 16, 30, 4);

        mListener.onEdgeAdded(16, 20, 14, 4);
        mListener.onEdgeAdded(16, 23, 15, 4);

        mListener.onEdgeAdded(17, 18, 21, 4);

        mListener.onEdgeAdded(18, 20, 10, 4);

        mListener.onEdgeAdded(19, 20, 12, 4);

        mListener.onEdgeAdded(20, 21, 10, 4);
        mListener.onEdgeAdded(20, 23, 13, 4);

        mListener.onEdgeAdded(21, 22, 11, 4);
        mListener.onEdgeAdded(21, 24, 11, 4);

        mListener.onEdgeAdded(22, 26, 16, 4);

        mListener.onEdgeAdded(23, 25, 13, 4);

        mListener.onEdgeAdded(24, 29, 11, 4);

        mListener.onEdgeAdded(25, 28, 11, 4);
        mListener.onEdgeAdded(25, 31, 10, 4);

        mListener.onEdgeAdded(26, 27, 11, 4);

        mListener.onEdgeAdded(27, 35, 15, 4);

        mListener.onEdgeAdded(28, 29, 11, 4);
        mListener.onEdgeAdded(28, 30, 11, 4);

        mListener.onEdgeAdded(29, 30, 12, 4);

        mListener.onEdgeAdded(30, 33, 12, 4);

        mListener.onEdgeAdded(31, 32, 10, 4);

        mListener.onEdgeAdded(33, 34, 11, 4);
        mListener.onEdgeAdded(33, 36, 16, 4);

        mListener.onEdgeAdded(34, 35, 12, 4);
        mListener.onEdgeAdded(34, 38, 17, 4);

        mListener.onEdgeAdded(35, 42, 23, 4);

        mListener.onEdgeAdded(36, 37, 11, 4);

        mListener.onEdgeAdded(37, 40, 12, 4);

        mListener.onEdgeAdded(38, 39, 10, 4);

        mListener.onEdgeAdded(39, 40, 11, 4);
        mListener.onEdgeAdded(39, 41, 16, 4);

        mListener.onEdgeAdded(40, 41, 19, 4);

        mListener.onEdgeAdded(41, 42, 23, 4);
        mListener.onEdgeAdded(41, 45, 20, 4);

        mListener.onEdgeAdded(42, 43, 20, 4);
        mListener.onEdgeAdded(42, 51, 50, 4);

        mListener.onEdgeAdded(43, 44, 12, 4);
        mListener.onEdgeAdded(43, 46, 12, 4);

        mListener.onEdgeAdded(44, 45, 11, 4);

        mListener.onEdgeAdded(45, 48, 27, 4);

        mListener.onEdgeAdded(46, 47, 21, 4);

        mListener.onEdgeAdded(47, 48, 11, 4);
        mListener.onEdgeAdded(47, 49, 22, 4);

        mListener.onEdgeAdded(48, 50, 28, 4);

        mListener.onEdgeAdded(49, 50, 16, 4);
        mListener.onEdgeAdded(49, 51, 21, 4);

        mListener.onEdgeAdded(50, 51, 12, 4);
        mListener.onEdgeAdded(50, 53, 27, 4);

        mListener.onEdgeAdded(51, 52, 17, 4);

        mListener.onEdgeAdded(52, 54, 19, 4);

        mListener.onEdgeAdded(53, 54, 13, 4);

    }
}

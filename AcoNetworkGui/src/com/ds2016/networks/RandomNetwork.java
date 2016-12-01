package com.ds2016.networks;

import com.ds2016.Range;

import java.util.Random;

/**
 * Created by wchee on 30/11/2016.
 */
public class RandomNetwork extends Network {
    private final int mNumNodes;
    private final double mProbability;
    private final Range<Integer> mCostRange;
    private final Range<Integer> mBandwidthRange;

    /**
     * Initializes a random network
     *
     * @param numNodes    The number of nodes
     * @param probability The probability for edge creation
     */
    public RandomNetwork(final int numNodes,
                         final double probability,
                         final Range<Integer> costRange,
                         final Range<Integer> bandwidthRange) {
        mNumNodes = numNodes;
        mProbability = probability;
        mCostRange = costRange;
        mBandwidthRange = bandwidthRange;
    }

    @Override
    public void build() {
        for (int i = 0; i < mNumNodes; i++) {
            mListener.onNodeAdded();
        }

        int v = 1;
        int w = -1;
        double lr;
        double lp = Math.log(1.0 - mProbability);
        Random random = new Random();
        while (v < mNumNodes) {
            lr = Math.log(1.0 - random.nextDouble());
            w = w + 1 + (int) (lr / lp);
            while (w >= v && v < mNumNodes) {
                w -= v;
                v += 1;
            }
            if (v < mNumNodes) {
                mListener.onEdgeAdded(v, w,
                        random.nextInt(mCostRange.getUpper()) + mCostRange.getLower(),
                        random.nextInt(mBandwidthRange.getUpper()) + mBandwidthRange.getLower());
            }
        }
    }
}

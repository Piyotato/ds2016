package com.ds2016.ui;

/**
 * Created by ds2016 on 13/6/16.
 */
public class ParameterStorage {

    private double mAlpha;
    private int mSource;
    private int mDestination;
    private int mAlgorithm;
    private double mInterval;
    private int mTraffic;
    private int mNumTicks;

    public ParameterStorage(final int source,
                            final int destination,
                            final double alpha,
                            final double interval,
                            final int traffic,
                            final int numTicks,
                            final int algorithm) {
        mSource = source;
        mDestination = destination;
        mAlpha = alpha;
        mInterval = interval;
        mTraffic = traffic;
        mNumTicks = numTicks;
        mAlgorithm = algorithm;
    }

    ParameterStorage(final int source,
                     final int destination,
                     final double alpha,
                     final double interval,
                     final int traffic,
                     final int numTicks) {
        mSource = source;
        mDestination = destination;
        mAlpha = alpha;
        mInterval = interval;
        mTraffic = traffic;
        mNumTicks = numTicks;
        mAlgorithm = 0;
    }

    public int getSource() {
        return mSource;
    }

    public void setSource(final int source) {
        mSource = source;
    }

    public int getDestination() {
        return mDestination;
    }

    public void setDestination(final int destination) {
        mDestination = destination;
    }

    public double getAlpha() {
        return mAlpha;
    }

    public void setAlpha(final double alpha) {
        mAlpha = alpha;
    }

    public double getInterval() {
        return mInterval;
    }

    public void setInterval(final double interval) {
        mInterval = interval;
    }

    public int getTraffic() {
        return mTraffic;
    }

    public void setTraffic(final int traffic) {
        mTraffic = traffic;
    }

    public int getAlgorithm() {
        return mAlgorithm;
    }

    public void setAlgorithm(final int algorithm) {
        mAlgorithm = algorithm;
    }

    public int getNumTicks() {
        return mNumTicks;
    }

    public void setNumTicks(final int numTicks) {
        mNumTicks = numTicks;
    }
}

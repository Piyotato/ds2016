package com.ds2016.ui;

/**
 * Created by ds2016 on 13/6/16.
 */
public class ParameterStorage {

    private double alpha;
    private int source;
    private int destination;
    private int algorithm;
    private double interval;
    private int traffic;

    public ParameterStorage(final double alpha,
                            final int traffic,
                            final double interval,
                            final int source,
                            final int destination,
                            final int algorithm) {
        this.alpha = alpha;
        this.traffic = traffic;
        this.interval = interval;
        this.source = source;
        this.destination = destination;
        this.algorithm = algorithm;
    }

    ParameterStorage(final double alpha,
                     final int traffic,
                     final double interval,
                     final int source,
                     final int destination) {
        this.alpha = alpha;
        this.traffic = traffic;
        this.interval = interval;
        this.source = source;
        this.destination = destination;
        this.algorithm = 0;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(final double alpha) {
        this.alpha = alpha;
    }

    public int getSource() {
        return source;
    }

    public void setSource(final int source) {
        this.source = source;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(final int destination) {
        this.destination = destination;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final int algorithm) {
        this.algorithm = algorithm;
    }

    public double getInterval() {
        return interval;
    }

    public void setInterval(final double interval) {
        this.interval = interval;
    }

    public int getTraffic() {
        return traffic;
    }

    public void setTraffic(final int traffic) {
        this.traffic = traffic;
    }
}

package com.ds2016;

/**
 * Created by ds2016 on 13/6/16.
 */
class ParameterStorage {
    private static final int ALGO_OSPF = 1;
    private static final int ALGO_ANTNET = 2;
    private static final int ALGO_EACO = 3;

    private int alpha;
    private int beta;
    private int ratio;
    private int tabuSize;
    private int source;
    private int destination;
    private int algorithm;
    private long data[];
    private long timeStamp[];

    ParameterStorage(int alpha, int beta,
                     int ratio, int tabuSize,
                     int source, int destination,
                     int algorithm, long[] data, long[] timeStamp) {
        this.ratio = ratio;
        this.alpha = alpha;
        this.beta = beta;
        this.tabuSize = tabuSize;
        this.source = source;
        this.destination = destination;
        this.algorithm = algorithm;
        this.data = data;
        this.timeStamp = timeStamp;
    }

    int getRatio() {
        return ratio;
    }

    void setRatio(String ratioStr) {
        if (ratioStr != null) ratio = Integer.parseInt(ratioStr);
    }

    int getAlpha() {
        return alpha;
    }

    void setAlpha(String alphaStr) {
        if (alphaStr != null) alpha = Integer.parseInt(alphaStr);
    }

    int getBeta() {
        return beta;
    }

    void setBeta(String betaStr) {
        if (betaStr != null) beta = Integer.parseInt(betaStr);
    }

    int getTabuSize() {
        return tabuSize;
    }

    void setTabuSize(String tabuSizeStr) {
        if (tabuSizeStr != null) tabuSize = Integer.parseInt(tabuSizeStr);
    }

    int getSource() {
        return source;
    }

    void setSource(int source) {
        this.source = source;
    }

    int getDestination() {
        return destination;
    }

    void setDestination(int destination) {
        this.destination = destination;
    }

    int getAlgorithm() {
        return algorithm;
    }

    void setAlgorithm(int algorithm) {
        if (isAlgorithmValid(algorithm)) this.algorithm = algorithm;
    }

    private boolean isAlgorithmValid(int algorithm) {
        return algorithm <= ALGO_EACO && algorithm >= ALGO_OSPF;
    }

    public long[] getData() {
        return data;
    }

    public void setData(long[] data) {
        this.data = data;
    }

    public long[] getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long[] timeStamp) {
        this.timeStamp = timeStamp;
    }
}

package com.ds2016;

/**
 * Created by ds2016 on 13/6/16.
 */
class ParameterStorage {
    static final int ALGO_OSPF = 1;
    static final int ALGO_ANTNET = 2;
    static final int ALGO_EACO = 3;

    private double alpha;
    private int tabuSize;
    private int source;
    private int destination;
    private int algorithm;

    ParameterStorage(double alpha,
                     int tabuSize,
                     int source,
                     int destination,
                     int algorithm) {
        this.alpha = alpha;
        this.tabuSize = tabuSize;
        this.source = source;
        this.destination = destination;
        this.algorithm = algorithm;
    }

    double getAlpha() {
        return alpha;
    }

    void setAlpha(String alphaStr) {
        if (alphaStr != null) alpha = Double.parseDouble(alphaStr);
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

    void setSource(String sourceStr) {
        if (sourceStr != null) source = Integer.parseInt(sourceStr);
    }

    int getDestination() {
        return destination;
    }

    void setDestination(String destinationStr) {
        if (destinationStr != null) destination = Integer.parseInt(destinationStr);
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
}

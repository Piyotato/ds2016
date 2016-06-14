package com.ds2016;

/**
 * Created by ds2016 on 13/6/16.
 */
class ParameterStorage {
    private double packToAntRatio;
    private double alphaWeightage;
    private double betaWeightage;
    private int tabuListSize;
    private int sourceNode;
    private int destinationNode;

    ParameterStorage(double packToAntRatio, double alphaWeightage,
                     double betaWeightage, int tabuListSize,
                     int sourceNode, int destinationNode) {
        this.packToAntRatio = packToAntRatio;
        this.alphaWeightage = alphaWeightage;
        this.betaWeightage = betaWeightage;
        this.tabuListSize = tabuListSize;
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
    }

    double getPackToAntRatio() {
        return packToAntRatio;
    }

    void setPackToAntRatio(String packToAntRatioStr) {
        setPackToAntRatio(packToAntRatioStr == null ? 1 : Double.parseDouble(packToAntRatioStr));
    }

    void setPackToAntRatio(double packToAntRatio) {
        this.packToAntRatio = packToAntRatio;
    }

    double getAlphaWeightage() {
        return alphaWeightage;
    }

    void setAlphaWeightage(String alphaWeightageStr) {
        setAlphaWeightage(alphaWeightageStr == null ? 1 : Double.parseDouble(alphaWeightageStr));
    }

    void setAlphaWeightage(double alphaWeightage) {
        this.alphaWeightage = alphaWeightage;
    }

    double getBetaWeightage() {
        return betaWeightage;
    }

    void setBetaWeightage(String betaWeightageStr) {
        setBetaWeightage(betaWeightageStr == null ? 1 : Double.parseDouble(betaWeightageStr));
    }

    void setBetaWeightage(double betaWeightage) {
        this.betaWeightage = betaWeightage;
    }

    int getTabuListSize() {
        return tabuListSize;
    }

    void setTabuListSize(String tabuListSizeStr) {
        setTabuListSize(tabuListSizeStr == null ? 1 : Integer.parseInt(tabuListSizeStr));
    }

    void setTabuListSize(int tabuListSize) {
        tabuListSize = tabuListSize;
    }

    int getSourceNode() {
        return sourceNode;
    }

    void setSourceNode(String sourceNodeStr) {
        setSourceNode(sourceNodeStr == null ? 1 : Integer.parseInt(sourceNodeStr));
    }

    void setSourceNode(int sourceNode) {
        this.sourceNode = sourceNode;
    }

    int getDestinationNode() {
        return destinationNode;
    }

    void setDestinationNode(String destinationNodeStr) {
        setDestinationNode(destinationNodeStr == null ? 1 : Integer.parseInt(destinationNodeStr));
    }

    void setDestinationNode(int destinationNode) {
        this.destinationNode = destinationNode;
    }
}

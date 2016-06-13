package com.ds2016;

/**
 * Created by ds2016 on 13/6/16.
 */
public class ParameterStorage {
    private double packToAntRatio;
    private double alphaWeightage;
    private double betaWeightage;
    private int TabuListSize;
    private int source;
    private int destinationNode;

    public double getPackToAntRatio() {
        return packToAntRatio;
    }

    public void setPackToAntRatio(double packToAntRatio) {
        this.packToAntRatio = packToAntRatio;
    }

    public double getAlphaWeightage() {
        return alphaWeightage;
    }

    public void setAlphaWeightage(double alphaWeightage) {
        this.alphaWeightage = alphaWeightage;
    }

    public double getBetaWeightage() {
        return betaWeightage;
    }

    public void setBetaWeightage(double betaWeightage) {
        this.betaWeightage = betaWeightage;
    }

    public int getTabuListSize() {
        return TabuListSize;
    }

    public void setTabuListSize(int tabuListSize) {
        TabuListSize = tabuListSize;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDestinationNode() {
        return destinationNode;
    }

    public void setDestinationNode(int destinationNode) {
        this.destinationNode = destinationNode;
    }
}

package com.ds2016;

import java.util.ArrayDeque;

/**
 * Created by damian on 30/11/16.
 */
public class ParametricModel {

    // Empirical Values
    // γ = Confidence Coefficient
    private final static double c1 = 0.7, c2 = 0.3, γ = 0.7, z = 1. / Math.sqrt(1 - γ);
    // η = Effective Samples
    private final static double η = 0.1, a = 1, c = 1;
    // w = Size of window (Dependant on value of η)
    private final static int w = (int) (5 * (c / η));
    private final ArrayDeque<Double> window = new ArrayDeque<>();
    private final ArrayDeque<Double> minDeque = new ArrayDeque<>();
    private final int N;
    // σ = Sample Mean
    // μ = Sample Variance
    // W = Sample Minimum
    private double σ, μ, W;

    /**
     * Build Parametric Model
     *
     * @param _N Number of neighbours
     */
    ParametricModel(int _N) {
        N = _N;
    }

    /**
     * Squash Function
     * Place emphasis on good results
     *
     * @param X Original Value
     * @return Squashed value
     */
    private double squash(double X) {
        return 1. / (1 + Math.exp(a / (X * N)));
    }

    /**
     * Get reinforcement
     *
     * @param T Time taken for trip
     * @return Reinforcement Value
     */
    double getReinforcement(double T) {
        while (!minDeque.isEmpty() && minDeque.peekLast() >= T) {
            minDeque.pollLast();
        }
        window.offerLast(T);
        minDeque.offerLast(T);
        if (window.size() > w) {
            if (window.peekFirst().equals(minDeque.peekFirst())) {
                minDeque.pollFirst();
            }
            window.pollFirst();
        }
        // Update Variables
        μ = μ + η * (T - μ);
        σ = σ + η * (Math.pow((T - σ), 2) - Math.pow(σ, 2));
        W = minDeque.peekFirst();
        // Calculate reinforcement
        double I_inf = W;
        double I_sup = μ + z * (σ / Math.sqrt(w));
        double r = c1 * (W / T) + c2 * ((I_sup - I_inf) / ((I_sup - I_inf) + (T - I_inf)));
        return squash(r) / squash(1);
    }
}

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
    // a and c have no empirical values...
    private final static double η = 0.1, a = 1, c = 1;
    // w = Size of window (Dependant on value of η)
    private final static int w = (int) (5 * (c / η));
    private final ArrayDeque<Double> window = new ArrayDeque<>();
    private final ArrayDeque<Double> minDeque = new ArrayDeque<>();
    private final int N;
    // μ = Sample Mean
    // σ2 = Sample Variance
    // W = Sample Minimum
    private double μ, σ2, W;

    /**
     * Build Parametric Model
     *
     * @param _N Number of neighbours
     */
    ParametricModel(int _N) {
        N = _N;
        System.out.println("Initialising...");
        System.out.println("N is: " + N);
        System.out.println("w is: " + w);
    }

    /**
     * Squash Function
     * Place emphasis on good results
     *
     * @param X Original Value
     * @return Squashed value
     * @throws IllegalArgumentException if X is out of the range (0, 1]
     */
    private double squash(double X) throws IllegalArgumentException {
        if (X > 1) throw new IllegalArgumentException("Got: " + X);
        return 1. / (1 + Math.exp(a / (X * N)));
    }

    /**
     * Get reinforcement
     *
     * @param T Time taken for trip
     * @return Reinforcement Value
     */
    double getReinforcement(double T) {
        while (!minDeque.isEmpty() && minDeque.peekLast() > T) {
            //System.out.println("Deleting: " + minDeque.peekLast());
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
        σ2 = σ2 + η * ((T - μ) * (T - μ) - σ2);
        W = minDeque.peekFirst();
        //System.out.println("New value of μ: " + μ);
        //System.out.println("New value of σ2: " + σ2);
        //System.out.println("New value of W: " + W);
        // Calculate reinforcement
        double I_inf = W;
        double I_sup = μ + z * (Math.sqrt(σ2) / Math.sqrt(w));
        //System.out.println("I_inf: " + I_inf);
        //System.out.println("I_sup: " + I_sup);
        //System.out.println(μ + " + " + z + " * " + (Math.sqrt(σ2) / Math.sqrt(w)));
        double r = c1 * (W / T) + c2 * ((I_sup - I_inf) / ((I_sup - I_inf) + (T - I_inf)));
        //System.out.println("Initial value of r: " + r);
        //System.out.println(c1 * (W / T) + " + " + c2 * ((I_sup - I_inf) / ((I_sup - I_inf) + (T - I_inf))));
        //System.out.println("squash(r): " + squash(r));
        //System.out.println("squash(1): " + squash(1));
        return squash(r) / squash(1);
    }
}

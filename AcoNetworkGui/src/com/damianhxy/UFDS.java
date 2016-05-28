package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class UFDS {

    private final ArrayList<Integer> P = new ArrayList<>(), RANK = new ArrayList<>();

    /**
     * Finds the set of an element
     *
     * @param node The element
     * @return The set of the element
     */
    private int findSet(int node) {
        return P.get(node) == node ? node : P.set(node, findSet(P.get(node)));
    }

    /**
     * Check whether two elements are
     * in the same set
     *
     * @param X First element
     * @param Y Second element
     * @return A boolean
     */
    boolean sameSet(int X, int Y) {
        return findSet(X) == findSet(Y);
    }

    /**
     * Merges the sets containing the elements
     *
     * @param X First element
     * @param Y Second element
     */
    void unionSet(int X, int Y) {
        X = findSet(X);
        Y = findSet(Y);
        if (X == Y) return;
        if (RANK.get(X) > RANK.get(Y)) {
            Collections.swap(RANK, X, Y);
            Collections.swap(P, X, Y);
        } else if (RANK.get(X).equals(RANK.get(Y))) {
            RANK.set(Y, RANK.get(Y) + 1);
        }
        P.set(X, Y);
    }

    /**
     * Initializes UFDS data structure
     *
     * @param N Number of elements
     */
    UFDS(int N) {
        for (int a = 0; a < N; ++a) {
            P.add(a);
            RANK.add(a, 1);
        }
    }
}

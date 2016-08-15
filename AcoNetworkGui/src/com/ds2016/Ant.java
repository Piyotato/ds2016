package com.ds2016;

import java.util.ArrayList;

/**
 * Created by damian on 16/5/16.
 */
class Ant extends Packet {

    final ArrayList<Double> timings = new ArrayList<>();
    double totalTime;
    boolean isBackwards;
    private final ArrayList<Integer> path = new ArrayList<>();

    /**
     * Initializes an ant
     *
     * @param source      Source node
     * @param destination Destination node
     * @param TTL         Time to live
     * @param creation    Time of creation
     */
    Ant(int source, int destination, int TTL, int creation) {
        super(source, destination, TTL, creation);
    }

    /**
     * Add a node to the path
     * Assumes that node is valid
     *
     * @param node Current node
     */
    void addNode(int node) {
        path.add(node);
        tabuList.add(node);
    }

    /**
     * Find the previous node on the
     * path of the backwards ant
     *
     * @return The previous node
     */
    int previousNode() {
        return path.remove(path.size() - 1);
    }

    /**
     * Find the next node on the
     * path of the backwards ant
     *
     * @return The next node
     */
    int nextNode() {
        return path.get(path.size() - 2);
    }

    /**
     * Calculate time taken for journey from
     * current node to destination
     */
    void updateTotalTime() {
        totalTime += timings.get(timings.size() - 1);
        timings.remove(timings.size() - 1);
        totalTime += timings.get(timings.size() - 1);
        timings.remove(timings.size() - 1);
    }
}

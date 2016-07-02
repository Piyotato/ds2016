package com.ds2016;

import java.util.ArrayList;

/**
 * Created by damian on 16/5/16.
 */
class Ant extends Packet {

    final ArrayList<Double> timings = new ArrayList<>();
    final ArrayList<Integer> path = new ArrayList<>();
    double totalTime;
    boolean isBackwards;

    /**
     * Initializes an ant
     *
     * @param _source Source node
     * @param _destination Destination node
     * @param _TTL Time to live
     * @param _creation Time of creation
     */
    Ant(int _source, int _destination, int _TTL, int _creation) {
        super(_source, _destination, _TTL, _creation);
        path.add(_source);
    }

    /**
     * Add a node to the path
     * Assumes that node is valid
     *
     * @param node Current node
     */
    void addNode(int node) {
        path.add(node);
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
        return path.get(path.size() - 1);
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

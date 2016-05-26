package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Ant extends Packet {

    double totalTime;
    ArrayList<Double> timings = new ArrayList<>();
    boolean isBackwards;

    /**
     * Initializes an ant
     *
     * @param _source Source node
     * @param _destination Destination node
     * @param _TTL Time to live
     */
    Ant(int _source, int _destination, int _TTL) {
        super(_source, _destination, _TTL);
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

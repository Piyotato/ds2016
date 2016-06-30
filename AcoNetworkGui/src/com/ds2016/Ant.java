package com.ds2016;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by damian on 16/5/16.
 */
class Ant extends Packet {

    final ArrayList<Double> timings = new ArrayList<>();
    private final HashMap<Integer, Boolean> tabuList = new HashMap<>();
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

    /**
     * Check if a node is currently
     * still in the Tabu list
     *
     * @param node Neighbouring node
     * @return Whether node is valid
     */
    boolean canVisit(int node) {
        return !tabuList.containsKey(node);
    }

    /**
     * Assuming a cycle exists,
     * calculate its size
     *
     * @param _neighbours Edges to neighbours
     * @return Size of cycle
     */
    int getCycleSize(Collection<Edge_ACO> _neighbours) {
        HashSet<Integer> neighbours = new HashSet<>();
        for (Edge_ACO edge: _neighbours) {
            neighbours.add(edge.destination);
        }
        int len = 0;
        int ptr = path.size() - 1;
        while (neighbours.size() > 0) {
            if (neighbours.contains(path.get(ptr))) { // Is a neighbour
                neighbours.remove(path.get(ptr));
            }
            ++len;
            --ptr;
        }
        return len;
    }

    /**
     * Delete the cycle
     *
     * @param _neighbours Edges to neighbours
     * @return Next node to visit
     */
    int deleteCycle(Collection<Edge_ACO> _neighbours) {
        HashSet<Integer> neighbours = new HashSet<>();
        for (Edge_ACO edge: _neighbours) {
            neighbours.add(edge.destination);
        }
        int last = -1;
        while (neighbours.size() > 0) {
            if (neighbours.contains(path.get(path.size() - 1))) { // Is a neighbour
                neighbours.remove(path.get(path.size() - 1));
                tabuList.remove(path.get(path.size() - 1));
                timings.remove(timings.size() - 1); // Edge
                timings.remove(timings.size() - 1); // Previous Node
                last = path.get(path.size() - 1);
            }
            path.remove(path.size() - 1);
        }
        return last;
    }
}

package com.damianhxy;

import java.util.*;

/**
 * Created by damian on 16/5/16.
 */
class Ant extends Packet {

    double totalTime;
    boolean isBackwards;
    ArrayList<Integer> path;

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
     * Add a node to the path
     * Assumes that node is valid
     *
     * @param node Current node
     */
    void addNode(int node) {
        path.add(node);
        if (node == destination) {
            isBackwards = true;
            path.add(null);
        }
    }

    /**
     * Find the previous node on the
     * path of the backwards ant
     *
     * @return The previous node
     */
    Integer previousNode() {
        int ID = path.get(path.size() - 1);
        path.remove(path.size() - 1);
        return ID;
    }

    /**
     * Check if a node is currently
     * blacklisted in the Tabu list
     *
     * @param node Neighbouring node
     * @param tabuSize Size of Tabu list
     * @return Whether node is valid
     */
    boolean isValid(int node, int tabuSize) {
        for (int a = 0; a < Math.min(tabuSize, path.size()); ++a) {
            if (path.get(path.size() - a - 1) == node) {
                return false;
            }
        }
        return true;
    }
}

package com.damianhxy;

/**
 * Created by damian on 16/5/16.
 */
class Ant extends Packet {

    double totalTime;
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
     * Add a node to the path
     * Assumes that node is valid
     * Overrides Packet.addNode()
     *
     * @param node Current node
     */
    void addNode(int node) {
        path.add(node);
        if (node == destination) {
            isBackwards = true;
        }
    }

    /**
     * Find the previous node on the
     * path of the backwards ant
     *
     * @return The previous node
     */
    int previousNode() {
        int ID = path.get(path.size() - 1);
        path.remove(path.size() - 1);
        return ID;
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
}

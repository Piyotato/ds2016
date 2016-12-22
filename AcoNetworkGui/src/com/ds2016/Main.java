package com.ds2016;

import com.ds2016.networks.Network;
import com.ds2016.networks.RandomNetwork;

/**
 * Created by zwliew on 13/6/16.
 */
public class Main {
    // Debug tunables
    public static final boolean DEBUG = false;

    // Algorithm tunables
    public static final int CHART_UPDATE_MS = 1000;
    // Debug table tunables
    public static final int NUM_ARRAY_ROWS = 60;
    public static final int NUM_ARRAY_COLS = 60;
    // GUI network tunables
    public static final String STYLE_SHEET =
            "edge.highLoad { fill-color: #F44336; }" +
                    "edge.midLoad { fill-color: #FF9800; }" +
                    "edge.lowLoad { fill-color: #8BC34A; }" +
                    "edge.minimalLoad { fill-color: #607D8B; }" +
                    "edge.noLoad { fill-color: #000000; }" +
                    "node.highLoad { fill-color: #F44336; }" +
                    "node.midLoad { fill-color: #FF9800; }" +
                    "node.lowLoad { fill-color: #8BC34A; }" +
                    "node.minimalLoad { fill-color: #607D8B; }" +
                    "node.noLoad { fill-color: #9E9E9E; }" +
                    "node { fill-color: #9E9E9E; }";
    // GUI network color algorithm tunables
    public static final double HIGH_LOAD_FACTOR = 3;
    public static final double MED_LOAD_FACTOR = 1.5;
    public static final double LOW_LOAD_FACTOR = 0.5;
    // GUI network shape tunables
    public static final Network GUI_NETWORK = new RandomNetwork(
            20, 0.2, Range.create(2, 15), Range.create(1000, 3000));
    static final int POLL_MS = 5;
    static final int TTL_MS = 1500;

    public static void main(String[] args) {
        final Link link = new Link();
        link.init();
    }
}

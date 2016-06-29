package com.ds2016;

import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;

import static com.ds2016.Main.mGraph;
import static com.ds2016.Main.mParams;

/**
 * Created by zwliew on 19/6/16.
 */
public class NewGui {
    private static final String STYLE_SHEET =
            "edge.highLoad { fill-color: red; }" +
                    "edge.midLoad { fill-color: orange; }" +
                    "edge.lowLoad { fill-color: black; }" +
                    "node.source { fill-color: green; }" +
                    "node.destination { fill-color: red; }";
    private static final String FRAME_TITLE = "EACO";
    private static final String GRAPH_TITLE = "Simulation";
    private static final String GRAPH_THREAD = "GRAPH_THREAD";
    static DynamicAlgorithm mAlgo;
    private JPanel mainPanel;
    private JTextField mTabuSizeField;
    private JTextField mAlphaField;
    private JTextField mDistanceField;
    private JTextField mFromField;
    private JTextField mToField;
    private JButton mAddBtn;
    private JButton mStartBtn;
    private JButton mStopBtn;
    private JButton mSaveBtn;
    private JRadioButton mOspfBtn;
    private JRadioButton mAntNetBtn;
    private JRadioButton mEAcoBtn;
    private JTextField mSourceField;
    private JTextField mDestinationField;
    private JTextField mToggleField;
    private JButton mToggleBtn;
    private JTextField mSpeedField;
    private Thread mThread;
    private GraphRunnable mRunnable;

    static void main() {
        JFrame frame = new JFrame(FRAME_TITLE);
        frame.setContentPane(new NewGui().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void initNetworkPanel() {
        mGraph = new SingleGraph(GRAPH_TITLE);
        mGraph.addAttribute("ui.stylesheet", STYLE_SHEET);
        mGraph.addNode("1").addAttribute("ui.label", 1);

        mAlgo = new GraphAlgo();
        mAlgo.init(mGraph);
        mAlgo.compute();

        mGraph.display();
    }

    private void updateParams() {
        mParams.setAlpha(mAlphaField.getText());
        mParams.setTabuSize(mTabuSizeField.getText());
        mParams.setSource(mSourceField.getText());
        mParams.setDestination(mDestinationField.getText());
    }

    private void updateTextFields() {
        mAlphaField.setText(String.valueOf(mParams.getAlpha()));
        mTabuSizeField.setText(String.valueOf(mParams.getTabuSize()));
        mSourceField.setText(String.valueOf(mParams.getSource()));
        mDestinationField.setText(String.valueOf(mParams.getDestination()));
    }

    private void createUIComponents() {
        mRunnable = new GraphRunnable();
        mThread = new Thread(mRunnable, GRAPH_THREAD);

        initNetworkPanel();

        /**
         * Set the current algorithm
         */
        mOspfBtn = new JRadioButton();
        mOspfBtn.addActionListener(actionEvent -> mParams.setAlgorithm(1));

        mAntNetBtn = new JRadioButton();
        mAntNetBtn.addActionListener(actionEvent -> mParams.setAlgorithm(2));

        mEAcoBtn = new JRadioButton();
        mEAcoBtn.addActionListener(actionEvent -> mParams.setAlgorithm(3));

        /**
         * Add a new node
         */
        mAddBtn = new JButton();
        mAddBtn.addActionListener(actionEvent -> {
            int from = Integer.parseInt(mFromField.getText());
            int to = Integer.parseInt(mToField.getText());
            int distance = Integer.parseInt(mDistanceField.getText());
            int speed = Integer.parseInt(mSpeedField.getText());
            addNode(from, to, distance, speed);
        });

        /**
         * Save parameters
         */
        mSaveBtn = new JButton();
        mSaveBtn.addActionListener(actionEvent -> {
            updateParams();
            updateTextFields();
            colouriseNodes();
        });

        /**
         * Start / stop the graph thread
         */
        mStartBtn = new JButton();
        mStartBtn.addActionListener(actionEvent -> {
            mThread = new Thread(mRunnable, GRAPH_THREAD);
            mThread.start();
        });

        mStopBtn = new JButton();
        mStopBtn.addActionListener(actionEvent -> mThread.interrupt());

        /**
         * Toggle node or edge
         */
        mToggleBtn = new JButton();
        mToggleBtn.addActionListener(actionEvent -> {
            String itemLabel = mToggleField.getText();
            if (itemLabel.contains("-")) {
                toggleEdge(itemLabel);
            } else {
                toggleNode(itemLabel);
            }
        });
    }

    /**
     * Colourise source and destination node
     */
    private void colouriseNodes() {
        Node source = mGraph.getNode(String.valueOf(mParams.getSource()));
        Node destination = mGraph.getNode(String.valueOf(mParams.getDestination()));
        for (Node n : mGraph) {
            if (n == source) {
                n.setAttribute("ui.class", "source");
            } else if (n == destination) {
                n.setAttribute("ui.class", "destination");
            } else {
                n.removeAttribute("ui.class");
            }
        }
    }

    private void addNode(int source, int destination, int cost, int speed) {
        if (mGraph.getNode(String.valueOf(destination)) == null) {
            Node node = mGraph.addNode(String.valueOf(destination));
            node.addAttribute("ui.label", destination);
            node.addAttribute("speed", speed);
        }
        if (mGraph.getEdge(String.valueOf(source + "-" + destination)) == null) {
            addEdge(source, destination, cost);
        }
    }

    private void addEdge(int source, int destination, int cost) {
        String edgeLabel = source + "-" + destination;
        if (mGraph.getEdge(edgeLabel) == null) {
            org.graphstream.graph.Edge edge = mGraph.addEdge(edgeLabel, String.valueOf(source), String.valueOf(destination));
            edge.addAttribute("ui.label", edgeLabel);
            edge.addAttribute("cost", cost);
        }
    }

    private void toggleNode(String label) {
        Node node = mGraph.getNode(label);
        if (node.hasAttribute("ui.hide")) {
            node.removeAttribute("ui.hide");
            for (Edge edge : node.getEachEdge()) {
                edge.removeAttribute("ui.hide");
            }
        } else {
            node.addAttribute("ui.hide");
            for (Edge edge : node.getEachEdge()) {
                edge.addAttribute("ui.hide");
            }
        }
    }

    private void toggleEdge(String label) {
        org.graphstream.graph.Edge edge = mGraph.getEdge(label);
        if (edge.hasAttribute("ui.hide")) {
            edge.removeAttribute("ui.hide");
        } else {
            edge.addAttribute("ui.hide");
        }
    }
}

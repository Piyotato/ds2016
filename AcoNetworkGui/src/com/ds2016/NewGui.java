package com.ds2016;

import org.graphstream.algorithm.DynamicAlgorithm;
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
                    "node.source { fill-color: green }" +
                    "node.destination { fill-color: red }";
    private static final String FRAME_TITLE = "EACO";
    private static final String GRAPH_TITLE = "Simulation";
    private static final String GRAPH_THREAD = "GRAPH_THREAD";
    static DynamicAlgorithm mAlgo;
    private JPanel mainPanel;
    private JTextField mRatioField;
    private JTextField mTabuSizeField;
    private JTextField mAlphaField;
    private JTextField mBetaField;
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
    private Thread mThread;
    private GraphRunnable mRunnable;

    void main() {
        JFrame frame = new JFrame(FRAME_TITLE);
        frame.setContentPane(new NewGui().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void initNetworkPanel() {
        mGraph = new SingleGraph(GRAPH_TITLE);
        mGraph.addAttribute("ui.stylesheet", STYLE_SHEET);
        mGraph.setStrict(false);
        mGraph.setAutoCreate(true);
        mGraph.addNode("1").addAttribute("ui.label", 1);

        mAlgo = new GraphAlgo();
        mAlgo.init(mGraph);
        mAlgo.compute();

        mGraph.display();
    }

    private void updateParams() {
        mParams.setRatio(mRatioField.getText());
        mParams.setAlpha(mAlphaField.getText());
        mParams.setBeta(mBetaField.getText());
        mParams.setTabuSize(mTabuSizeField.getText());
        mParams.setSource(Integer.parseInt(mSourceField.getText()));
        mParams.setDestination(Integer.parseInt(mDestinationField.getText()));
    }

    private void updateTextFields() {
        mRatioField.setText(String.valueOf(mParams.getRatio()));
        mAlphaField.setText(String.valueOf(mParams.getAlpha()));
        mBetaField.setText(String.valueOf(mParams.getBeta()));
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
            String from = mFromField.getText();
            String to = mToField.getText();
            String distance = mDistanceField.getText();
            addNode(from, to, distance);
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
    }

    /**
     * Colourise source and destination node
     */
    private void colouriseNodes() {
        Node source = mGraph.getNode(mParams.getSource());
        Node destination = mGraph.getNode(mParams.getDestination());
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

    private void addNode(String from, String to, String distance) {
        if (mGraph.getNode(to) != null &&
                mGraph.getEdge(from + to) != null) {
            return;
        }
        if (mGraph.getNode(to) == null) {
            mGraph.addNode(to).addAttribute("ui.label", to);
        }
        addEdge(from, to, distance);
    }

    private void addEdge(String from, String to, String distance) {
        if (mGraph.getEdge(from + to) == null) {
            org.graphstream.graph.Edge edge = mGraph.addEdge(from + to, from, to);
            edge.addAttribute("ui.label", from + to + " - " + distance);
            edge.addAttribute("length", distance);
        }
    }
}

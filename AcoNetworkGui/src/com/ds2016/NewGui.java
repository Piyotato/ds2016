package com.ds2016;

import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;
import java.util.ArrayList;

import static com.ds2016.Main.sGraph;
import static com.ds2016.Main.sParams;

/**
 * Created by zwliew on 19/6/16.
 */
public class NewGui {
    private static final String GRAPH_THREAD = "GRAPH_THREAD";
    private static final String STYLE_SHEET =
            "edge.highLoad { fill-color: red; }" +
                    "edge.midLoad { fill-color: orange; }" +
                    "edge.lowLoad { fill-color: black; }" +
                    "node.source { fill-color: green; }" +
                    "node.destination { fill-color: red; }";
    private static final String FRAME_TITLE = "EACO";
    private static final String GRAPH_TITLE = "Simulation";
    static DynamicAlgorithm sGraphAlgo;
    ArrayList<Node_GUI> mNodeList = new ArrayList<>();
    ArrayList<SimpleEdge> mEdgeList = new ArrayList<>();
    private JPanel mainPanel;
    private JTextField mTabuSizeField;
    private JTextField mAlphaField;
    private JTextField mDistanceField;
    private JTextField mFromField;
    private JTextField mToField;
    private JButton mAddNodeBtn;
    private JButton mAddEdgeBtn;
    private JButton mStartBtn;
    private JButton mStopBtn;
    private JButton mTickBtn;
    private JRadioButton mOspfBtn;
    private JRadioButton mAntNetBtn;
    private JRadioButton mEAcoBtn;
    private JTextField mSourceField;
    private JTextField mDestinationField;
    private JTextField mToggleEdgeField;
    private JButton mToggleEdgeBtn;
    private JTextField mSpeedField;
    private JButton mUpdateBtn;
    private JButton mToggleNodeBtn;
    private JTextField mToggleNodeField;
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
        sGraph = new SingleGraph(GRAPH_TITLE);
        //sGraph.setStrict(false);
        //sGraph.setAutoCreate(true);
        sGraph.addAttribute("ui.stylesheet", STYLE_SHEET);

        sGraphAlgo = new GraphAlgo();
        sGraphAlgo.init(sGraph);
        //sGraphAlgo.compute();

        sGraph.display();
    }

    private void createUIComponents() {
        mRunnable = new GraphRunnable();
        mThread = new Thread(mRunnable, GRAPH_THREAD);

        initNetworkPanel();

        /**
         * Set the current algorithm
         */
        mOspfBtn = new JRadioButton();
        mOspfBtn.addActionListener(actionEvent -> Link.setAlgorithm(1));

        mAntNetBtn = new JRadioButton();
        mAntNetBtn.addActionListener(actionEvent -> Link.setAlgorithm(2));

        mEAcoBtn = new JRadioButton();
        mEAcoBtn.addActionListener(actionEvent -> Link.setAlgorithm(3));

        /**
         * Add a new node
         */
        mAddNodeBtn = new JButton();
        mAddNodeBtn.addActionListener(actionEvent -> Link.addNode(Integer.parseInt(mSpeedField.getText())));

        /**
         * Toggle Node
         */
        mToggleNodeBtn = new JButton();
        mToggleNodeBtn.addActionListener(actionEvent -> Link.toggleNode(Integer.parseInt(mToggleNodeField.getText())));

        /**
         * Add a new edge
         */
        mAddEdgeBtn = new JButton();
        mAddEdgeBtn.addActionListener(actionEvent -> {
            int from = Integer.parseInt(mFromField.getText());
            int to = Integer.parseInt(mToField.getText());
            int distance = Integer.parseInt(mDistanceField.getText());
            Link.addEdge(from, to, distance);
        });

        /**
         * Toggle edge
         */
        mToggleEdgeBtn = new JButton();
        mToggleEdgeBtn.addActionListener(actionEvent -> {
            Link.toggleEdge(Integer.parseInt(mToggleEdgeField.getText()));
        });

        /**
         * Save parameters
         */
        mUpdateBtn = new JButton();
        mUpdateBtn.addActionListener(actionEvent -> {
            Link.update();
        });

        /**
         * Start the sGraph algorithm thread
         */
        mStartBtn = new JButton();
        mStartBtn.addActionListener(actionEvent -> {

            Link.start();
        });

        /**
         * Stop the sGraph algorithm thread
         */
        mStopBtn = new JButton();
        mStopBtn.addActionListener(actionEvent -> {
            Link.stop();
        });

        /**
         * Undergo one tick of the entire program (sGraph + algorithm)
         */
        mTickBtn = new JButton();
        mTickBtn.addActionListener(actionEvent -> {
            Link.tick();
        });
    }

    void startThread() {
        mThread = new Thread(mRunnable, GRAPH_THREAD);
        mThread.start();
    }

    void stopThread() {
        mThread.interrupt();
    }

    void update() {
        updateParams();
        updateTextFields();
        colouriseNodes();
    }

    /**
     * Update ParameterStorage parameters
     */
    private void updateParams() {
        sParams.setAlpha(mAlphaField.getText());
        sParams.setTabuSize(mTabuSizeField.getText());
        sParams.setSource(mSourceField.getText());
        sParams.setDestination(mDestinationField.getText());
    }

    /**
     * Update text in text fields
     */
    private void updateTextFields() {
        mAlphaField.setText(String.valueOf(sParams.getAlpha()));
        mTabuSizeField.setText(String.valueOf(sParams.getTabuSize()));
        mSourceField.setText(String.valueOf(sParams.getSource()));
        mDestinationField.setText(String.valueOf(sParams.getDestination()));
    }

    /**
     * Colourise source and destination node
     */
    private void colouriseNodes() {
        Node source = sGraph.getNode(String.valueOf(sParams.getSource()));
        Node destination = sGraph.getNode(String.valueOf(sParams.getDestination()));
        for (Node n : sGraph) {
            if (n == source) {
                n.setAttribute("ui.class", "source");
            } else if (n == destination) {
                n.setAttribute("ui.class", "destination");
            } else {
                n.removeAttribute("ui.class");
            }
        }
    }

    /**
     * Add a new node
     *
     * @param speed Processing speed
     */
    void addNode(int speed) {
        // Add GUI node
        int nodeCount = sGraph.getNodeCount();
        Node node = sGraph.addNode(String.valueOf(nodeCount));
        node.addAttribute("ui.label", nodeCount);
        node.addAttribute("speed", speed);

        // Add Node_GUI node for algorithm to read from
        Node_GUI listNode = new Node_GUI(speed);
        mNodeList.add(listNode);
    }

    /**
     * Toggle state of a node
     *
     * @param ID Node ID
     * @throws IllegalArgumentException
     */
    void toggleNode(int ID) throws IllegalArgumentException {
        if (ID == sParams.getSource() || ID == sParams.getDestination()) {
            throw new IllegalArgumentException();
        }
        Node node = sGraph.getNode(ID);

        // Toggle sGraph state
        if (node.hasAttribute("ui.hide")) {
            node.removeAttribute("ui.hide");
        } else {
            node.addAttribute("ui.hide");
        }

        // Toggle Node_GUI state
        Node_GUI listNode = mNodeList.get(ID);
        listNode.isOffline ^= true;
    }

    /**
     * Add a bidirectional edge
     *
     * @param node1 First node
     * @param node2 Second node
     * @param cost  Time taken
     * @throws IllegalArgumentException
     */
    void addEdge(int node1, int node2, int cost) throws IllegalArgumentException {
        // We can't add edges between non-existent nodes...or can we?
        int nodeCount = sGraph.getNodeCount();
        if (node1 >= nodeCount || node2 >= nodeCount) {
            throw new IllegalArgumentException();
        }

        // Add forward edge
        int edgeCount = sGraph.getEdgeCount() / 2;
        Edge forward = sGraph.addEdge(String.valueOf(edgeCount + "f"), node1, node2, true);
        forward.addAttribute("ui.label", String.valueOf(edgeCount));
        forward.addAttribute("edge.cluster", String.valueOf(edgeCount));
        forward.addAttribute("cost", cost);

        // Add backward edge
        Edge backward = sGraph.addEdge(String.valueOf(edgeCount + "b"), node2, node1, true);
        backward.addAttribute("edge.cluster", String.valueOf(edgeCount));
        backward.addAttribute("cost", cost);

        // Add edges to the SimpleEdge list
        mEdgeList.add(new SimpleEdge(node1, node2, cost));
        mEdgeList.add(new SimpleEdge(node2, node1, cost));
    }

    /**
     * Toggle state of an edge
     *
     * @param ID Edge ID
     */
    void toggleEdge(int ID) {
        Edge forward = sGraph.getEdge(ID * 2 + "f");
        Edge backward = sGraph.getEdge(ID * 2 + "b");

        // Toggle forward edge visibility
        if (forward.hasAttribute("ui.hide")) {
            forward.removeAttribute("ui.hide");
        } else {
            forward.addAttribute("ui.hide");
        }

        // Toggle backward edge visibility
        if (backward.hasAttribute("ui.hide")) {
            backward.removeAttribute("ui.hide");
        } else {
            backward.addAttribute("ui.hide");
        }
    }
}

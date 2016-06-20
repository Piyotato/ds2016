package com.ds2016;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;

import static com.ds2016.Main.mParams;

/**
 * Created by ds2016 on 25/3/16.
 *
 * Old, use NewGui!
 *
 */
class Gui extends JFrame {

    private static final String FRAME_TITLE = "ACO Network Simulation";
    private static final int TEXTFIELD_COLUMN = 10;

    private JTextField mPackToAntRatioField;
    private JTextField mAlphaWeightageField;
    private JTextField mBetaWeightageField;
    private JTextField mTabuListSizeField;
    private JTextField mSourceField;
    private JTextField mDestinationField;

    private LineGraph mSuccessGraph;
    private LineGraph mThroughputGraph;

    private JPanel mGraphPanel;

    private Gui() {
        super(FRAME_TITLE);
        initComponents();
    }

    void init() {
        SwingUtilities.invokeLater(() -> new Gui().setVisible(true));
    }


    private void initComponents() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(FRAME_TITLE);
        setSize(896, 504); // Completely arbitrary 16:9 ratio divisible by 8

        JPanel networkPanel = initNetworkPanel();
        JPanel configPanel = initConfigPanel();
        JPanel container = initContainer(networkPanel, configPanel);
        setContentPane(container);
    }

    /**
     * Initializes a new container for containing all the UI
     * elements in the program
     *
     * @param panels The JPanels that hold the smaller UI elements
     * @return the newly initialized container
     */
    private JPanel initContainer(JPanel... panels) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        for (JPanel panel : panels) {
            container.add(panel);
        }

        return container;
    }

    /**
     * Initializes a new network panel for displaying
     * the router nodes being simulated
     *
     * @return the newly initialized network panel
     */
    private JPanel initNetworkPanel() {
        JPanel panel = new JPanel();
        Graph graph = new SingleGraph("Tutorial 1");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");
        graph.display();

        return panel;
    }

    /**
     * Initializes a new config panel for displaying
     * the config options being controlled
     *
     * @return the newly initialized config panel
     */
    private JPanel initConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel graphConfigs = initGraphConfigs();
        JPanel miscConfigs = initMiscConfigs();
        panel.add(graphConfigs);
        panel.add(miscConfigs);

        return panel;
    }

    /**
     * Initializes a new graph configurations panel
     * for displaying the graphs
     *
     * @return the newly initialized graph config panel
     */
    private JPanel initGraphConfigs() {
        mGraphPanel = new JPanel();
        mGraphPanel.setLayout(new BoxLayout(mGraphPanel, BoxLayout.X_AXIS));

        mSuccessGraph = new LineGraph("Success rate");
        mThroughputGraph = new LineGraph("Throughput");
        ChartPanel successPanel = mSuccessGraph.createLineGraph(null, null);
        ChartPanel throughputPanel = mThroughputGraph.createLineGraph(null, null);

        mGraphPanel.add(successPanel);
        mGraphPanel.add(throughputPanel);
        return mGraphPanel;
    }

    /**
     * Initializes a new miscellaneous configuration panel
     * for displaying the other config options
     *
     * @return the newly initialized misc config panel
     */
    private JPanel initMiscConfigs() {
        JPanel configs = new JPanel();
        configs.setLayout(new BoxLayout(configs, BoxLayout.X_AXIS));
        configs.setMaximumSize(new Dimension(448, 504));

        mPackToAntRatioField = new JTextField(TEXTFIELD_COLUMN);
        mAlphaWeightageField = new JTextField(TEXTFIELD_COLUMN);
        mBetaWeightageField = new JTextField(TEXTFIELD_COLUMN);
        mTabuListSizeField = new JTextField(TEXTFIELD_COLUMN);
        mSourceField = new JTextField(TEXTFIELD_COLUMN);
        mDestinationField = new JTextField(TEXTFIELD_COLUMN);

        mPackToAntRatioField.setMaximumSize(new Dimension(100, 20));

        GuiPanel leftPanel = new GuiPanel();
        leftPanel.setVerticalAxis(true);
        leftPanel.addParameterField("Packet : ants ratio", mPackToAntRatioField);
        leftPanel.addLabel("Relative weightage");
        leftPanel.addLabel("Source / destination");

        GuiPanel rightPanel = new GuiPanel();
        rightPanel.setVerticalAxis(true);
        rightPanel.addParameterField("Size of tabu list", mTabuListSizeField);
        rightPanel.addParameterField("α", mAlphaWeightageField);
        rightPanel.addParameterField("β", mBetaWeightageField);
        rightPanel.add(mSourceField);
        rightPanel.add(mDestinationField);

        JButton saveButton = new JButton();
        saveButton.setText("Save config");
        saveButton.addActionListener(actionEvent -> {
            mParams.setRatio(mPackToAntRatioField.getText());
            mParams.setAlpha(mAlphaWeightageField.getText());
            mParams.setBeta(mBetaWeightageField.getText());
            mParams.setTabuSize(mTabuListSizeField.getText());
            mParams.setSource(Integer.parseInt(mSourceField.getText()));
            mParams.setDestination(Integer.parseInt(mDestinationField.getText()));

            mPackToAntRatioField.setText(String.valueOf(mParams.getRatio()));
            mAlphaWeightageField.setText(String.valueOf(mParams.getAlpha()));
            mBetaWeightageField.setText(String.valueOf(mParams.getBeta()));
            mTabuListSizeField.setText(String.valueOf(mParams.getTabuSize()));
            mSourceField.setText(String.valueOf(mParams.getSource()));
            mDestinationField.setText(String.valueOf(mParams.getDestination()));
        });
        rightPanel.add(saveButton);

        configs.add(leftPanel);
        configs.add(rightPanel);

        return configs;
    }

    private void updateGraphPanel(ChartPanel successPanel, ChartPanel throughputPanel) {
        mGraphPanel.removeAll();
        mGraphPanel.invalidate();
        mGraphPanel.add(successPanel);
        mGraphPanel.add(throughputPanel);
        mGraphPanel.revalidate();
        mGraphPanel.repaint();
    }
}

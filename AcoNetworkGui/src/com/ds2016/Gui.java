package com.ds2016;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ds2016.Main.params;

/**
 * Created by ds2016 on 25/3/16.
 */
class Gui extends JFrame {

    private static final String FRAME_TITLE = "ACO Network Simulation";

    private JButton saveButton;
    private JTextField packToAntRatioField;
    private JTextField alphaWeightageField;
    private JTextField betaWeightageField;
    private JTextField tabuListSizeField;
    private JTextField sourceField;
    private JTextField destinationField;


    private Gui() {
        initComponents();
    }

    static void main(String[] args) {
        // Initialize GUI in an Event-Dispatching thread for thread-safety
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

        /*
         * TODO: Initialize the graph
         */

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
        JPanel panel = new JPanel();
        // TODO: Initialize the graphs
        return panel;
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

        packToAntRatioField = new JTextField(10);
        alphaWeightageField = new JTextField(10);
        betaWeightageField = new JTextField(10);
        tabuListSizeField = new JTextField(10);
        sourceField = new JTextField(10);
        destinationField = new JTextField(10);

        GuiPanel leftPanel = new GuiPanel();
        leftPanel.setVerticalAxis(true);
        leftPanel.addParameterField("Packet : ants ratio", packToAntRatioField);
        leftPanel.addLabel("Relative weightage");
        leftPanel.addLabel("Source / destination");

        GuiPanel rightPanel = new GuiPanel();
        rightPanel.setVerticalAxis(true);
        rightPanel.addParameterField("Size of tabu list", tabuListSizeField);
        rightPanel.addParameterField("α", alphaWeightageField);
        rightPanel.addParameterField("β", betaWeightageField);
        rightPanel.add(sourceField);
        rightPanel.add(destinationField);

        saveButton = new JButton();
        saveButton.setText("Save config");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                params.setPackToAntRatio(packToAntRatioField.getText());
                params.setAlphaWeightage(alphaWeightageField.getText());
                params.setBetaWeightage(betaWeightageField.getText());
                params.setTabuListSize(tabuListSizeField.getText());
                params.setSourceNode(sourceField.getText());
                params.setDestinationNode(destinationField.getText());

                packToAntRatioField.setText(String.valueOf(params.getPackToAntRatio()));
                alphaWeightageField.setText(String.valueOf(params.getAlphaWeightage()));
                betaWeightageField.setText(String.valueOf(params.getBetaWeightage()));
                tabuListSizeField.setText(String.valueOf(params.getTabuListSize()));
                sourceField.setText(String.valueOf(params.getSourceNode()));
                destinationField.setText(String.valueOf(params.getDestinationNode()));
            }
        });
        rightPanel.add(saveButton);

        configs.add(leftPanel);
        configs.add(rightPanel);

        return configs;
    }

}

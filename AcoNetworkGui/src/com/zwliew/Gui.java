package com.zwliew;

import javax.swing.*;

/**
 * Created by zwliew on 25/3/16.
 */
public class Gui extends JFrame {

    private static final String FRAME_TITLE = "ACO Network Simulation";

    private Gui() {
        initComponents();
    }

    public static void main(String[] args) {
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
     * @param panels The JPanels that hold the smaller UI elemtnts
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

        /*
         * TODO: Initialize the graph
         */

        return new JPanel();
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
        // TODO: Initialize the graphs
        return new JPanel();
    }

    /**
     * Initializes a new miscellaneous configuration panel
     * for displaying the other config options
     *
     * @return the newly initialized misc config panel
     */
    private JPanel initMiscConfigs() {
        JPanel configs = new JPanel();
        configs.setLayout(new BoxLayout(configs, BoxLayout.Y_AXIS));

        GuiPanel firstPanel = new GuiPanel();
        firstPanel.addLabel("Packet : ants ratio");
        firstPanel.addTextField(10);
        firstPanel.addLabel("Size of tabu list");
        firstPanel.addTextField(10);

        GuiPanel secondPanel = new GuiPanel();
        secondPanel.addLabel("Relative weightage");
        secondPanel.addLabel("α");
        secondPanel.addTextField(10);
        secondPanel.addLabel("β");
        secondPanel.addTextField(10);

        GuiPanel thirdPanel = new GuiPanel();
        thirdPanel.addLabel("Source / destination");
        thirdPanel.addTextField(10);
        thirdPanel.addTextField(10);

        configs.add(firstPanel);
        configs.add(secondPanel);
        configs.add(thirdPanel);
        return configs;
    }
}

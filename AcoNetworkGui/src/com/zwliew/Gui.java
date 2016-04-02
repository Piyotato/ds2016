package com.zwliew;

import javax.swing.*;
import java.awt.*;

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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Gui().setVisible(true);
            }
        });
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
        JPanel panel = new JPanel();

        panel.setPreferredSize(new Dimension(538, 504));

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

        panel.setPreferredSize(new Dimension(358, 504));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        /*
         * TODO: Add in all the config options
         */

        return panel;
    }
}

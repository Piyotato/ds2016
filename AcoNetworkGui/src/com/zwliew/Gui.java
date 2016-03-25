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
        // Exit program when GUI is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Initialize the main panel
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        // Initialize the 2 sub-panels
        JPanel networkPanel = new JPanel();
        JPanel configPanel = new JPanel();

        // Allocate a width ratio of about 3 to 2
        networkPanel.setPreferredSize(new Dimension(538, 504));
        configPanel.setPreferredSize(new Dimension(358, 504));

        // TODO: Populate the sub-panels
        networkPanel.add(new JLabel("NETWORK PANEL"));
        configPanel.add(new JLabel("CONFIG PANEL"));

        // Populate the content pane with the panels
        container.add(networkPanel);
        container.add(configPanel);
        setContentPane(container);

        // Set title and frame size
        setTitle(FRAME_TITLE);
        setSize(896, 504); // Completely arbitrary 16:9 ratio divisible by 8

    }
}

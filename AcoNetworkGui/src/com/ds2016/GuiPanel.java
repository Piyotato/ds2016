package com.ds2016;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ds2016 on 2/4/16.
 */
final class GuiPanel extends JPanel {
    /**
     * Adds a JLabel to the panel
     *
     * @param string Text to be displayed
     */
    void addLabel(String string) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(string);
        panel.add(label, BorderLayout.NORTH);
        add(panel);
    }

    /**
     * Adds a JTextField to the panel
     *
     * @param columns number of columns to be displayed
     */
    void addTextField(int columns) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField(columns);
        panel.add(textField, BorderLayout.NORTH);
        add(panel);
    }

    void addParameterField(String labelString) {
        addParameterField(labelString, new JTextField(10));
    }

    void addParameterField(String labelString, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel(labelString));
        panel.add(textField);
        add(panel);
    }

    /**
     * Sets the BoxLayout axis of the panel
     *
     * @param vertical whether the axis is vertical or horizontal
     */
    void setVerticalAxis(boolean vertical) {
        int axis = vertical ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS;
        setLayout(new BoxLayout(this, axis));
    }
}

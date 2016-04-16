package com.zwliew;

import javax.swing.*;

/**
 * Created by zwliew on 2/4/16.
 */
final class GuiPanel extends JPanel {
    /**
     * Adds a JLabel to the panel
     *
     * @param string Text to be displayed
     */
    void addLabel(String string) {
        JLabel label = new JLabel(string);
        add(label);
    }

    /**
     * Adds a JTextField to the panel
     *
     * @param columns number of columns to be displayed
     */
    void addTextField(int columns) {
        JTextField textField = new JTextField(columns);
        add(textField);
    }

    /**
     * Sets the BoxLayout axis of the panel
     *
     * @param vertical whether the axis is vertical or horizontal
     */
    public void setAxis(boolean vertical) {
        int axis = vertical ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS;
        setLayout(new BoxLayout(this, axis));
    }
}

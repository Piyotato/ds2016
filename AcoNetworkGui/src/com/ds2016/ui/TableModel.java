package com.ds2016.ui;

import com.ds2016.Main;

import javax.swing.table.DefaultTableModel;

/**
 * Created by zwliew on 6/12/16.
 */
public class TableModel extends DefaultTableModel {

    private final int mNode;
    private String[][] mData = new String[Main.NUM_ARRAY_ROWS][Main.NUM_ARRAY_COLS];

    TableModel(final int node) {
        mNode = node;
        resetData();
    }

    @Override
    public int getRowCount() {
        return mData == null ? 0 : mData.length;
    }

    @Override
    public int getColumnCount() {
        return mData == null ? 0 : mData[0].length;
    }

    @Override
    public String getColumnName(int col) {
        return mData[0][col];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public Object getValueAt(int row, int col) {
        return mData[row][col];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (Main.DEBUG) System.out.println("setValueAt(): row=" + row + " col=" + col + " value=" + value);
        mData[row][col] = String.valueOf(value);
        fireTableCellUpdated(row, col);
    }

    void resetData() {
        for (int row = 0; row < Main.NUM_ARRAY_ROWS; row++) {
            for (int col = 0; col < Main.NUM_ARRAY_COLS; col++) {
                mData[row][col] = "";
            }
        }
    }
}

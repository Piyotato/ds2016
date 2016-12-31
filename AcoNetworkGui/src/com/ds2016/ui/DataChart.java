package com.ds2016.ui;

import com.ds2016.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class DataChart {
    private static final String FRAME_TITLE = "Data charts";
    private static final String THROUGHPUT_TITLE = "Throughput";
    private static final String CHART_X = "Elapsed time";
    private static final String CHART_THROUGHPUT_Y = "Packets received";

    private JTabbedPane mTabbedPane;
    private XYSeries mThroughputSeries;
    private int mUpdateCnt;
    private int mNumNodes;
    private long mAccumulatedThroughput;
    private long mElapsedTicks;

    private java.util.List<TableModel> mModelList = new ArrayList<>();

    private long mConsistentCount;
    private long mTickCount;
    private boolean mLoggedMax;

    void display() {
        JFrame frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mTabbedPane = new JTabbedPane();
        mTabbedPane.add(THROUGHPUT_TITLE, throughputPane());
        frame.add(mTabbedPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        frame.add(panel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private ChartPanel throughputPane() {
        mThroughputSeries = new XYSeries("Throughput data");
        final XYSeriesCollection dataset = new XYSeriesCollection(mThroughputSeries);
        final JFreeChart chart = ChartFactory.createXYLineChart(THROUGHPUT_TITLE, CHART_X, CHART_THROUGHPUT_Y,
                dataset, PlotOrientation.VERTICAL, false, false, false);
        final ChartPanel panel = new ChartPanel(chart);

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final double width = screenSize.getWidth() / 3.3;
        final double height = screenSize.getHeight() / 3.75;
        panel.setPreferredSize(new Dimension((int) width, (int) height));
        return panel;
    }

    private void updatePheromoneTables() {
        for (int i = 0; i < mNumNodes; i++) {
            int row = 1;
            final TableModel model = mModelList.get(i);
            if (Link.sAlgorithm instanceof EACO) {
                final Node_EACO node = ((EACO) Link.sAlgorithm).nodes.get(i);
                for (Map.Entry<Integer, HashMap<Integer, Double>> entry : node.pheromone.M.entrySet()) {
                    final HashMap<Integer, Double> value = entry.getValue();
                    int col = 1;
                    for (Map.Entry<Integer, Double> valueEntry : value.entrySet()) {
                        model.setValueAt(valueEntry.getKey(), 0, col);
                        model.setValueAt(String.valueOf(valueEntry.getValue()), row, col);
                        col++;
                    }
                    final int key = entry.getKey();
                    model.setValueAt(key, row, 0);
                    row++;
                }
            } else if (Link.sAlgorithm instanceof AntNet) {
                final Node_AntNet node = ((AntNet) Link.sAlgorithm).nodes.get(i);
                for (Map.Entry<Integer, HashMap<Integer, Double>> entry : node.pheromone.M.entrySet()) {
                    final HashMap<Integer, Double> value = entry.getValue();
                    int col = 1;
                    for (Map.Entry<Integer, Double> valueEntry : value.entrySet()) {
                        model.setValueAt(valueEntry.getKey(), 0, col);
                        model.setValueAt(String.valueOf(valueEntry.getValue()), row, col);
                        col++;
                    }
                    final int key = entry.getKey();
                    model.setValueAt(key, row, 0);
                    row++;
                }
            } else if (Link.sAlgorithm instanceof OSPF) {
                final Node_OSPF node = ((OSPF) Link.sAlgorithm).nodes.get(i);
                for (ArrayList<Integer> value : node.SSSP.P) {
                    int col = 1;
                    for (Integer valueEntry : value) {
                        model.setValueAt(col, 0, col);
                        model.setValueAt(String.valueOf(valueEntry), row, col);
                        col++;
                    }
                    model.setValueAt(row - 1, row, 0);
                    row++;
                }
            }
        }
    }

    void updateCharts() {
        updatePheromoneTables();

        mAccumulatedThroughput += Link.sThroughput;

        if (++mElapsedTicks < Main.NUM_TICKS_PER_CHART_UPDATE) {
            return;
        }

        mThroughputSeries.add(mThroughputSeries.getItemCount(), mAccumulatedThroughput);

        if (Main.DEBUG_THROUGHPUT) {
            ++mTickCount;
            if (mAccumulatedThroughput >= Main.DEBUG_PACKETS_PER_TICK * 950) {
                ++mConsistentCount;
                if (mConsistentCount == 5) {
                    System.out.println("95% at " + (mTickCount - 5));
                }
            } else {
                if (mConsistentCount >= 5) {
                    System.out.println("95% RESET!");
                }
                mConsistentCount = 0;
            }
            if (!mLoggedMax && mAccumulatedThroughput >= Main.DEBUG_PACKETS_PER_TICK * 1000) {
                System.out.println("100% at " + mTickCount);
                mLoggedMax = true;
            }
        }

        mAccumulatedThroughput = 0;
        mElapsedTicks = 0;
    }

    void resetCharts() {
        mThroughputSeries.clear();
        mThroughputSeries.add(0, 0);
        mUpdateCnt = 0;
        mAccumulatedThroughput = 0;
        mElapsedTicks = 0;
        for (int node = 0; node < mNumNodes; node++) {
            final TableModel model = mModelList.get(node);
            model.resetData();
        }
        updatePheromoneTables();

        mTickCount = 0;
        mConsistentCount = 0;
        mLoggedMax = false;
    }

    void addNode() {
        if (!Main.DISPLAY_PHEROMONE) {
            return;
        }
        final TableModel model = new TableModel(mNumNodes);
        mModelList.add(model);
        final JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        mTabbedPane.add("Node " + String.valueOf(mNumNodes), new JScrollPane(table));
        mNumNodes++;
    }

    void toggleNode(final boolean isOffline) {
        mNumNodes += isOffline ? -1 : 1;
    }
}
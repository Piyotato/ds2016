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
    private int mUpdateCnt = 0;
    private int mNumNodes = 0;

    private java.util.List<TableModel> mModelList = new ArrayList<>();

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
                Node_EACO node = ((EACO) Link.sAlgorithm).nodes.get(i);
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
                Node_AntNet node = ((AntNet) Link.sAlgorithm).nodes.get(i);
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
            } else {
                // Todo: OSPF
            }
        }
    }

    void updateCharts() {
        if (Main.DEBUG) System.out.println(++mUpdateCnt + " " + Link.sThroughput);
        mThroughputSeries.add(mThroughputSeries.getItemCount(), Link.sThroughput);
        updatePheromoneTables();
    }

    void resetCharts() {
        mThroughputSeries.clear();
        mUpdateCnt = 0;
    }

    void addNode() {
        TableModel model = new TableModel(mNumNodes);
        mModelList.add(model);
        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        mTabbedPane.add("Node " + String.valueOf(mNumNodes), new JScrollPane(table));
        mNumNodes++;
    }
}
package com.ds2016;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

import static com.ds2016.Main.sTickVal;

class DataChart {
    private static final String FRAME_TITLE = "Data charts";
    private static final String THROUGHPUT_TITLE = "Throughput";
    private static final String SUCCESS_TITLE = "Success rate";
    private static final String CHART_X = "Elapsed time";
    private static final String CHART_THROUGHPUT_Y = "Packets received";
    private static final String CHART_SUCCESS_Y = "Successs";

    private long mThroughput;
    private double mSuccessRate;

    private XYSeries mThroughputSeries;
    private XYSeries mSuccessSeries;

    void display() {
        JFrame frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(THROUGHPUT_TITLE, throughputPane());
        tabbedPane.add(SUCCESS_TITLE, successPane());
        frame.add(tabbedPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        frame.add(panel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private ChartPanel throughputPane() {
        mThroughputSeries = new XYSeries("Throughput data");
        XYSeriesCollection dataset = new XYSeriesCollection(mThroughputSeries);
        JFreeChart chart = ChartFactory.createXYLineChart(THROUGHPUT_TITLE, CHART_X, CHART_THROUGHPUT_Y,
                dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth() / 3.3;
        double height = screenSize.getHeight() / 3.75;
        panel.setPreferredSize(new Dimension((int) width, (int) height));
        return panel;
    }

    private ChartPanel successPane() {
        mSuccessSeries = new XYSeries("Success rate data");
        XYSeriesCollection dataset = new XYSeriesCollection(mSuccessSeries);
        JFreeChart chart = ChartFactory.createXYLineChart(SUCCESS_TITLE, CHART_X, CHART_SUCCESS_Y,
                dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth() / 3.3;
        double height = screenSize.getHeight() / 3.75;
        panel.setPreferredSize(new Dimension((int) width, (int) height));
        return panel;
    }

    void updateCharts() {
        updateStats();
        if (mThroughput != -1 && mSuccessRate != -1) {
            mThroughputSeries.add(mThroughputSeries.getItemCount(), mThroughput);
            mSuccessSeries.add(mSuccessSeries.getItemCount(), mSuccessRate);
        }
    }

    void resetCharts() {
        sTickVal = null;
        mThroughputSeries.clear();
        mSuccessSeries.clear();
    }

    private void updateStats() {
        if (sTickVal == null) return;
        long success = sTickVal.getKey();
        long failure = sTickVal.getValue();
        if (success + failure > 0) {
            mThroughput = success;
            mSuccessRate = failure > 0 ? (double) success / (failure + success) * 100 : 100;
        } else {
            mThroughput = -1;
            mSuccessRate = -1;
        }
        System.out.println("updateStats(): mSuccessRate: " + mSuccessRate);
    }
}
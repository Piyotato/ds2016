package com.ds2016;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Created by zwliew on 14/6/16.
 */
public class LineGraph {
    private String title;
    private ChartPanel panel;
    private JFreeChart graph;

    LineGraph(String title) {
        this.title = title;
    }

    public DefaultCategoryDataset createDataSet(long[] data, long[] timeStamp) {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        if (data != null && timeStamp != null) {
            for (int i = 0; i < data.length; i++) {
                dataSet.addValue(data[i], "Rate", String.valueOf(timeStamp[i]));
            }
        }
        return dataSet;
    }

    public ChartPanel createLineGraph(long[] data, long[] timeStamp) {
        graph = ChartFactory.createLineChart(
                title,
                null,
                null,
                createDataSet(data, timeStamp),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        panel = new ChartPanel(graph);
        panel.setVisible(true);
        return panel;
    }

    public ChartPanel updateGraph(long[] data, long[] timeStamp) {
        graph = ChartFactory.createLineChart(
                title,
                null,
                null,
                createDataSet(data, timeStamp),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        panel = new ChartPanel(graph);
        panel.setVisible(true);
        return panel;
    }
}

package ru.sherb.chart;

import lombok.Value;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.Styler;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * @author maksim
 * @since 23.03.19
 */
public final class XChartRenderer {

    @Value
    static class ChartData {
        String name;
        Number value;
    }

    public BufferedImage createPieChart(String title, ChartData... data) {
        PieChart chart = new PieChartBuilder()
                .title(title)
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        Arrays.stream(data)
                .forEach(d -> chart.addSeries(d.getName(), d.getValue()));

        return BitmapEncoder.getBufferedImage(chart);
    }
}

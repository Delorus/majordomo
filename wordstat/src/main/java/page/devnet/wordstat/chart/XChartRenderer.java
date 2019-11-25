package page.devnet.wordstat.chart;

import lombok.Value;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.CategoryStyler;
import org.knowm.xchart.style.Styler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @author maksim
 * @since 23.03.19
 */
public final class XChartRenderer {

    public Chart render(Renderable renderable, String titlePostfix) {
        return renderable.renderBy(this, titlePostfix);
    }

    @Value
    static class PieChartData {
        String name;
        Number value;
    }

    Chart createPieChart(String title, PieChartData... data) {
        PieChart chart = new PieChartBuilder()
                .title(title)
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        Arrays.stream(data)
                .forEach(d -> chart.addSeries(d.getName(), d.getValue()));

        chart.getStyler().setLegendVisible(true);

        return new Chart(BitmapEncoder.getBufferedImage(chart));
    }

    @Value
    static class BarChartData {
        String name;
        Map<String, Integer> values;
    }

    Chart createBarChart(String title, BarChartData... data) {
        CategoryChart chart = new CategoryChartBuilder()
                .title(title)
                .xAxisTitle("user") //todo
                .yAxisTitle("count")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        CategoryStyler styler = chart.getStyler();
        styler.setLegendPosition(Styler.LegendPosition.OutsideS);
        styler.setOverlapped(true);
        styler.setLegendVisible(true);
        if (data != null && data.length >= 1 && data[0].getValues().size() >= 3) {
            styler.setXAxisLabelRotation(10);
        }

        Color[] sliceColors = new Color[] { new Color(133, 33, 120), new Color(195, 245, 0) };
        styler.setSeriesColors(sliceColors);

        for (BarChartData d : data) {
            chart.addSeries(d.name, new ArrayList<>(d.values.keySet()), new ArrayList<>(d.values.values()));
        }

        return new Chart(BitmapEncoder.getBufferedImage(chart));
    }
}

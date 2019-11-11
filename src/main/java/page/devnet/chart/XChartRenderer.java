package page.devnet.chart;

import lombok.Value;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.Styler;

import java.util.Arrays;

/**
 * @author maksim
 * @since 23.03.19
 */
final class XChartRenderer {

    @Value
    static class ChartData {
        String name;
        Number value;
    }

    public Chart createPieChart(String title, ChartData... data) {
        PieChart chart = new PieChartBuilder()
                .title(title)
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        Arrays.stream(data)
                .forEach(d -> chart.addSeries(d.getName(), d.getValue()));

        return new Chart(BitmapEncoder.getBufferedImage(chart));
    }
}

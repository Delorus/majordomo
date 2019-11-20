package page.devnet.wordstat.chart;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.CategoryStyler;
import org.knowm.xchart.style.Styler;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author maksim
 * @since 23.03.19
 */
@Disabled("manual start")
public class ExampleXChartLib {

    @Test
    void saveInPNG() throws IOException {
        double[] xData = new double[]{0.0, 1.0, 2.0};
        double[] yData = new double[]{2.0, 1.0, 0.0};

        // Create Chart
        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

        // Save it
        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);
    }

    @Test
    void saveBarChart() throws IOException {
        CategoryChart chart = new CategoryChartBuilder()
                .title("Test Word Count Diagram")
                .xAxisTitle("user")
                .yAxisTitle("count")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        CategoryStyler styler = chart.getStyler();
        styler.setLegendPosition(Styler.LegendPosition.OutsideS);
        styler.setOverlapped(true);

        chart.addSeries("all word", Arrays.asList("user1", "user2", "user3"), Arrays.asList(10, 2, 4));
        chart.addSeries("unique word", Arrays.asList("user1", "user2", "user3"), Arrays.asList(3, 1, 3));

        BitmapEncoder.saveBitmap(chart, "./Word_Count_Chart", BitmapEncoder.BitmapFormat.PNG);
    }

    @Test
    void savePieChart() throws IOException {
        PieChart chart = new PieChartBuilder()
                .title("Test Word Frequency Diagram")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        chart.addSeries("apple", .3);
        chart.addSeries("lol", .25);
        chart.addSeries("keks", .05);
        chart.addSeries("other", .4);

        chart.getStyler().setLegendVisible(true);

        BitmapEncoder.saveBitmap(chart, "./Word_Frequency_Chart", BitmapEncoder.BitmapFormat.PNG);
    }
}

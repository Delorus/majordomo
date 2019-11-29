package page.devnet.wordstat.chart;

import java.util.List;

/**
 * @author maksim
 * @since 23.03.19
 */
interface Renderable {

    Chart renderBy(XChartRenderer renderService, String titlePostfix);

    List<Chart> renderList(XChartRenderer rendererService, String titlePostfix);
}

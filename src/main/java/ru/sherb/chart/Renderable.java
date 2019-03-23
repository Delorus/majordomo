package ru.sherb.chart;

import java.awt.image.BufferedImage;

/**
 * @author maksim
 * @since 23.03.19
 */
interface Renderable {

    BufferedImage renderBy(XChartRenderer renderService);
}

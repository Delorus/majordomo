package ru.sherb.chart;

import java.awt.image.BufferedImage;

/**
 * @author maksim
 * @since 23.03.19
 */
public interface Renderable {

    BufferedImage renderBy(XChartRenderer renderService);
}

package ru.sherb.chart;

/**
 * @author maksim
 * @since 23.03.19
 */
interface Renderable {

    Chart renderBy(XChartRenderer renderService);
}
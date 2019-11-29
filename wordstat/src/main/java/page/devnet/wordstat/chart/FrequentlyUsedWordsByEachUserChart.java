package page.devnet.wordstat.chart;

import java.util.*;

/**
 * create by
 * Konstantin
 */
//TODO
public final class FrequentlyUsedWordsByEachUserChart implements Renderable {

    //Each user has list top words;
    private static final class EachUserTopWordCount {
        private final String user;
        private final List<String> wordsUser;

        public EachUserTopWordCount(String user, List wordsUser) {
            this.user = user;
            this.wordsUser = wordsUser;
        }

        public String getUser() {
            return user;
        }

        public List<String> getWordsUser() {
            return wordsUser;
        }
    }

    private final List<EachUserTopWordCount> eachUserTopWordCountList = new ArrayList<>();

    public void addUser(String user, List<String> wordsUser) {
        eachUserTopWordCountList.add(new EachUserTopWordCount(user, wordsUser));
    }


    @Override
    public Chart renderBy(XChartRenderer renderService, String titlePostfix) {
        return null;
    }

    @Override
    public List<Chart> renderList(XChartRenderer renderService, String titlePostfix) {

        List<Chart> charts = new ArrayList<>();

        for (int i =0; i<eachUserTopWordCountList.size();i++) {
            XChartRenderer.BarChartEachUserData top10EachUser = new XChartRenderer.
                    BarChartEachUserData(eachUserTopWordCountList.get(i).user, eachUserTopWordCountList.get(i).wordsUser);

            charts.add(renderService.createBarChartEachUserData("Top 10 words by users " + eachUserTopWordCountList.get(i) + " :" + titlePostfix,
                    top10EachUser));
        }
// return list charts
        return charts;
    }




}
/*
int numCharts = 10;

        List<CategoryChart> charts = new ArrayList<CategoryChart>();

        for (int i = 0; i < numCharts; i++) {

            CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("User ").xAxisTitle("Score").yAxisTitle("Number").build();

            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
            chart.getStyler().setHasAnnotations(true);

            chart.addSeries("user 1", Arrays.asList(new String[] { "String", "String 2", "String 2", "String 2", "String 2", "String 2", "String 2"}), Arrays.asList(new Integer[] { 4, 5, 0, 15, 2, 6, 8}));
            chart.addSeries("user 2", Arrays.asList(new String[] { "String", "String 2", "String 2", "String 2", "String 2", "String 2", "String 2"}), Arrays.asList(new Integer[] { 4, 5, 0, 5, 2, 6, 8}));

            charts.add(chart);
        }
        new SwingWrapper<CategoryChart>(charts).displayChartMatrix();
    }
 */
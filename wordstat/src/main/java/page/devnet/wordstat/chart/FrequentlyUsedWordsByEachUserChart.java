package page.devnet.wordstat.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create by
 * Konstantin
 */
//TODO
public final class FrequentlyUsedWordsByEachUserChart implements Renderable{

    //Each user has list top words;
    private static final class EachUserTopWordCount{
        private final String user;
        private final Map<String,Integer> eachUserTop10Word;

        public EachUserTopWordCount(String user, Map<String,Integer> eachUserTop10Word) {
            this.user = user;
            this.eachUserTop10Word = eachUserTop10Word;
        }

        public String getUser() {
            return user;
        }

        public Map<String, Integer> getEachUserTop10Word() {
            return eachUserTop10Word;
        }
    }

    private final List<EachUserTopWordCount> eachUserTopWordCountList = new ArrayList<>();

    public void addUser(String user, Map<String, Integer> eachUserTop10Word) {
        eachUserTopWordCountList.add(new EachUserTopWordCount(user, eachUserTop10Word));
    }


    @Override
    public Chart renderBy(XChartRenderer renderService, String titlePostfix) {

        for (int i=0; i<10; i++){
            for (Map.Entry<String, Integer> entry : eachUserTopWordCountList.get(i).eachUserTop10Word.entrySet()) {
                Map<String,Integer> map = new HashMap<>();
                map.put(eachUserTopWordCountList.get(i).user,entry.getValue());
            //add 1 param: unique, 2 param: Hashmap user,int
            XChartRenderer.BarChartData top10EachUser = new XChartRenderer.BarChartData(entry.getKey(),map);
            }
        }
        return null;//renderService.createBarChart2(" ", );
    }


}

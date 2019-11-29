package page.devnet.wordstat.chart;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleListChart {


    @Test
    void createChart(){
        List<String > list = new ArrayList<>();
        List<String> listUser =new ArrayList<>();
        FrequentlyUsedWordsByEachUserChart chart = new FrequentlyUsedWordsByEachUserChart();
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (int i=0; i<10; i++){
            listUser.add("user"+i);
            if(i==i%2) {
                hashMap.put("String" + 2,i+i%2);
            }else {
                hashMap.put("String", i);
            }
        }

        for (int i=0; i<10; i++){
            chart.addUser(listUser.get(i), hashMap);
        }
        XChartRenderer xChartRenderer = new XChartRenderer();
        //Assertions.assertEquals(10,chart.renderList(xChartRenderer, "").size());

        List<Chart> chartList = new ArrayList<>();
        chartList = xChartRenderer.renders(chart,"postfix");
        //TODO How to check List<myChart>
        for(int i =0; i<chartList.size(); i++)
        {
            //BitmapEncoder.saveBitmap(chartList.get(i), "./Word_Count_Chart", BitmapEncoder.BitmapFormat.PNG);
        }


    }
}

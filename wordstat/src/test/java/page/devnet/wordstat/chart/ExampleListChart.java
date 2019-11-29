package page.devnet.wordstat.chart;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
            for (int j=0; j<10; j++) {
                if (i == i % 2) {
                    hashMap.put("String" + 2, i+j);
                } else {
                    hashMap.put("String", i);
                }
            }
            System.out.println(hashMap);
        }
        System.out.println(hashMap);
        for (int i=0; i<10; i++){
            chart.addUser(listUser.get(i), hashMap);
        }
        XChartRenderer xChartRenderer = new XChartRenderer();
        for (int i=0;i<listUser.size();i++){
            XChartRenderer.BarChartEachUserData top10EachUser = new XChartRenderer.
                    BarChartEachUserData(listUser.get(i),hashMap);
            xChartRenderer.createBarChartEachUserData("title",top10EachUser);

        }

        List<Chart> chartList = new ArrayList<>();
        chartList = xChartRenderer.renders(chart,"postfix");
        //TODO How to check List<myChart>
        for(int i =0; i<chartList.size(); i++)
        {
            Path path = Paths.get("/work/chart"+i+".png");
            Files file = null;
            try {
                if (file.exists(path)){
                    file.delete(path);
                }
                file.createDirectories(path.getParent());
                file.createFile(path);
                InputStream in = chartList.get(i).toInputStream();
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("absolute" + path.getFileName());
        }


    }
}

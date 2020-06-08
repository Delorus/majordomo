package page.devnet.database.repository.impl;

import org.mapdb.Serializer;
import page.devnet.database.DataSource;
import page.devnet.database.repository.WordStorageRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordStorageImpl implements WordStorageRepository {

    private static final String TABLE_NAME_DATE_TO_WORDS = "dateToWords";
    private static final String TABLE_NAME_USER_TO_DATE = "userToDate";

    private final Map<Instant, List<String>> dateToWordsTable;

    private final Map<String, List<Instant>> userToDateTable;
    private final DataSource dataSource;

    public WordStorageImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dateToWordsTable = dataSource.getDatabase().hashMap(TABLE_NAME_DATE_TO_WORDS)
                .keySerializer(Serializer.JAVA)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen();
        this.userToDateTable = dataSource.getDatabase().hashMap(TABLE_NAME_USER_TO_DATE)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen();
    }

    @Override
    public void storeAll(String userId, Instant date, List<String> words) {
        dateToWordsTable.put(date, words);
        //TODO list is different, userToDateTable.value is empty;
        System.out.println(userId + " " + date);
        System.out.println(userToDateTable.size());
        System.out.println(userToDateTable + " " + userToDateTable.hashCode());
        userToDateTable.computeIfAbsent(userId, __ -> {
            List<Instant> list = new ArrayList<>();
            System.out.println("work" + list.hashCode());
            return list;
        }).add(date);
        System.out.println(userToDateTable.values());
        dataSource.getDatabase().commit();
    }

    @Override
    public List<String> findAllWordsFrom(Instant fromDate) {
        var result = new ArrayList<String>();
        dateToWordsTable.entrySet().stream()
                .filter(x -> x.getKey().isAfter(fromDate))
                .forEach((date)-> result.addAll(date.getValue()));
        /*dateToWordsTable.forEach((date, words) -> {
           // System.out.println(date);
            if (date.isAfter(fromDate)) {
                result.addAll(words);
            }
        });*/
        return result;
    }

    @Override
    public Map<String, List<String>> findAllWordsByUserFrom(Instant fromDate) {
        var result = new HashMap<String, List<String>>();
        userToDateTable.forEach((user, dates) -> {
            List<String> words;
            words = dates.stream()
                    .filter(fromDate::isBefore)
                    .flatMap(date -> dateToWordsTable.get(date).stream())
                    .collect(Collectors.toList());
            result.put(user, words);
        });
        return result;
    }
}

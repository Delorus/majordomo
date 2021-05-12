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

    public static final String TABLE_DATE_TO_WORDS = "dateToWords";
    public static final String TABLE_USER_TO_DATE = "userToDate";

    private final DataSource dataSource;

    private final Map<Instant, List<String>> dateToWordsTable;
    private final Map<String, List<Instant>> userToDateTable;

    public WordStorageImpl(DataSource dataSource) {
        this(dataSource, TABLE_DATE_TO_WORDS, TABLE_USER_TO_DATE);
    }

    WordStorageImpl(DataSource dataSource, String dateToWordsTable, String userToDateTable) {
        this.dataSource = dataSource;
        this.dateToWordsTable = dataSource.getDatabase().hashMap(dateToWordsTable)
                .keySerializer(Serializer.JAVA)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen();
        this.userToDateTable = dataSource.getDatabase().hashMap(userToDateTable)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen();
    }

    @Override
    public void storeAll(String userId, Instant date, List<String> words) {
        dateToWordsTable.put(date, words);
        List<Instant> list = new ArrayList<>();
        List<Instant> listToUpdateUser = userToDateTable.computeIfAbsent(userId, __ -> list);
        listToUpdateUser.add(date);
        userToDateTable.put(userId, listToUpdateUser);
        dataSource.getDatabase().commit();
    }

    @Override
    public List<String> findAllWordsFrom(Instant fromDate) {
        var result = new ArrayList<String>();
        dateToWordsTable.keySet().forEach(date -> {
            if (date.isAfter(fromDate)) {
                result.addAll(dateToWordsTable.get(date));
            }
        });
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Empty words in base");
        }
        return result;
    }

    @Override
    public Map<String, List<String>> findAllWordsByUserFrom(Instant fromDate) {
        var result = new HashMap<String, List<String>>();
        userToDateTable.forEach((user, dates) -> result.put(user, dates.stream()
                .filter(fromDate::isBefore)
                .flatMap(date -> dateToWordsTable.get(date).stream())
                .collect(Collectors.toList())));
        if (result.values().isEmpty()) {
            throw new IllegalArgumentException("Empty words in base");
        }
        return result;
    }
}

package page.devnet.database.repository.impl;

import org.mapdb.Serializer;
import page.devnet.database.DataSource;
import page.devnet.database.repository.WordStorageRepository;
import page.devnet.database.serializer.SerializerInstant;
import page.devnet.database.serializer.SerializerList;
import page.devnet.database.serializer.SerializerListInstant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordStorageImpl implements WordStorageRepository {

    private static final String TABLE_NAME_DATE_TO_WORDS = "dateToWords";
    private static final String TABLE_NAME_USER_TO_DATE = "UserToDate";

    private final Map<Instant, List<String>> dateToWordsTable;

    private final Map<String, List<Instant>> userToDateTable;
    //private final Set<Message> messages;
    private final DataSource dataSource;

    public WordStorageImpl(DataSource dataSource) {
        this.dataSource = dataSource;
     //   this.messages =  dataSource.getDatabase().hashSet("test").serializer(new SerializerMessage()).createOrOpen();
        this.dateToWordsTable = dataSource.getDatabase().hashMap(TABLE_NAME_DATE_TO_WORDS).keySerializer(new SerializerInstant())
                .valueSerializer(new SerializerList()).createOrOpen();
        this.userToDateTable = dataSource.getDatabase().hashMap(TABLE_NAME_USER_TO_DATE).keySerializer(Serializer.STRING)
                .valueSerializer(new SerializerListInstant()).createOrOpen();

    }

    @Override
    public void storeAll(String userId, Instant date, List<String> words) {
      //  messages.add(new Message(userId,date,words));
        if (userId==null){
            System.out.println("userid");}
        if (date == null){
            System.out.println("date");}
        if (words == null){
            System.out.println("words");
        }
        dateToWordsTable.put(date, words);
        userToDateTable.computeIfAbsent(userId, __ -> new ArrayList<>()).add(date);
        dataSource.getDatabase().commit();
    }

    @Override
    public List<String> findAllWordsFrom(Instant fromDate) {
        var result = new ArrayList<String>();
        /*dateToWordsTable.forEach((date, words) -> {
            if (date.isAfter(fromDate)) {
                result.addAll(words);
            }
        });*/
        return result;
    }

    @Override
    public List<String> flushAll() {
        return null;
    }

    @Override
    public Map<String, List<String>> findAllWordsByUserFrom(Instant fromDate) {
        var result = new HashMap<String, List<String>>();
      /*  userToDateTable.forEach((user, dates) -> {
            List<String> words = dates.stream()
                    .filter(fromDate::isBefore)
                    .flatMap(date -> dateToWordsTable.get(date).stream())
                    .collect(Collectors.toList());
            result.put(user, words);
        });*/
        return result;
    }
}

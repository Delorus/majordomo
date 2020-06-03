package page.devnet.database.repository.impl;

import org.mapdb.Serializer;
import page.devnet.database.DataSource;
import page.devnet.database.entity.Message;
import page.devnet.database.repository.WordStorageRepository;
import page.devnet.database.serializer.SerializerInstant;
import page.devnet.database.serializer.SerializerList;
import page.devnet.database.serializer.SerializerListInstant;

import java.time.Instant;
import java.util.*;

public class WordStorageImpl implements WordStorageRepository {

    //private static final String TABLE_NAME = "message";

    //private final Set<Message> messagesTable;
    private static final String TABLE_NAME_DATE_TO_WORDS = "dateToWords";
    private static final String TABLE_NAME_USER_TO_DATE = "UserToDate";

    private final Map<Instant, List<String>> dateToWordsTable;

    private final Map<String, List<Instant>> userToDateTable;

    private final DataSource dataSource;

    public WordStorageImpl(DataSource dataSource) {
        this.dataSource = dataSource;
     //   this.messagesTable = dataSource.getDatabase().hashSet(TABLE_NAME).serializer(new SerializerMessage()).createOrOpen();
        this.dateToWordsTable = dataSource.getDatabase().hashMap(TABLE_NAME_DATE_TO_WORDS).keySerializer(new SerializerInstant())
                .valueSerializer(new SerializerList()).createOrOpen();
        this.userToDateTable = dataSource.getDatabase().hashMap(TABLE_NAME_USER_TO_DATE).keySerializer(Serializer.STRING)
                .valueSerializer(new SerializerListInstant()).createOrOpen();
    }

    @Override
    public void storeAll(String userId, Instant date, List<String> words) {
        //messagesTable.add(new Message(userId,date,words));
        dateToWordsTable.put(date,words);
        dataSource.getDatabase().commit();
    }

    @Override
    public List<String> findAllWordsFrom(Instant fromDate) {
        var result = new ArrayList<String>();
        dateToWordsTable.forEach((date, words) -> {
            if (date.isAfter(fromDate)) {
                result.addAll(words);
            }
        });
        return result;
    }

    @Override
    public List<String> flushAll() {
        return null;
    }

    @Override
    public Map<String, List<String>> findAllWordsByUserFrom(Instant from) {
        return null;
    }
}

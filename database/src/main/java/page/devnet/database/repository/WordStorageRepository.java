package page.devnet.database.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface WordStorageRepository {

    void storeAll(String userId, Instant date, List<String> words);

    List<String> findAllWordsFrom(Instant from);

    Map<String, List<String>> findAllWordsByUserFrom(Instant from);

}

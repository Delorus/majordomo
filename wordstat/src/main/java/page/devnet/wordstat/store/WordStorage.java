package page.devnet.wordstat.store;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author maksim
 * @since 19.11.2019
 */
public interface WordStorage {

    void storeAll(String userId, Instant date, List<String> words);

    List<String> findAllWordsFrom(Instant from);

    List<String> flushAll();

    Map<String, List<String>> findAllWordsByUserFrom(Instant from);
}

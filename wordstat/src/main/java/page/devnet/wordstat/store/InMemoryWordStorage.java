package page.devnet.wordstat.store;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maksim
 * @since 19.11.2019
 */
public class InMemoryWordStorage implements WordStorage {

    //todo it's not thread safe
    private final Map<Instant, List<String>> dateToWords = new HashMap<>();
    private final Map<String, Instant> userToDate = new HashMap<>();

    @Override
    public void storeAll(String userId, Instant date, List<String> words) {
        dateToWords.put(date, words);
        userToDate.put(userId, date);
    }

    @Override
    public List<String> findAllWordsFrom(Instant from) {
        var result = new ArrayList<String>();
        dateToWords.forEach((date, words) -> {
            if (from.isAfter(date)) {
                result.addAll(words);
            }
        });

        return result;
    }

    @Override
    public List<String> flushAll() {
        var result = new ArrayList<String>();
        userToDate.forEach((user, date) -> {
            List<String> words = dateToWords.get(date);
            result.add(user + ";" + date.getEpochSecond() + ";" + String.join(";", words));
        });

        // because little memory
        dateToWords.clear();
        userToDate.clear();
        return result;
    }
}

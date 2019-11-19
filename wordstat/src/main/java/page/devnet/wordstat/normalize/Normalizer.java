package page.devnet.wordstat.normalize;

/**
 * @author maksim
 * @since 19.11.2019
 */
public interface Normalizer {

    static Normalizer forRussian() {
        return new RussianNormalizer();
    }

    static Normalizer forEnglish() {
        return new EnglishNormalizer();
    }

    String normalize(String word);
}

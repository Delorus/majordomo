package page.devnet.wordstat.normalize;

/**
 * @author maksim
 * @since 19.11.2019
 */
class RussianNormalizer implements Normalizer {

    @Override
    public String normalize(String word) {
        word = word.toLowerCase();
        return word;
    }
}

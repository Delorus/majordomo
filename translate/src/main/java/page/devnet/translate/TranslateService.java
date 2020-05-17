package page.devnet.translate;

/**
 * @author maksim
 * @since 02.03.19
 */
public interface TranslateService {
    String transRuToEn(String text) throws TranslateException;

    String transEnToRu(String text) throws TranslateException;
}

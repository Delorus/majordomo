package page.devnet.translate;

import java.io.IOException;

/**
 * @author maksim
 * @since 02.03.19
 */
public interface TranslateService {
    String transRuToEn(String text) throws IOException;

    String transEnToRu(String text) throws IOException;
}

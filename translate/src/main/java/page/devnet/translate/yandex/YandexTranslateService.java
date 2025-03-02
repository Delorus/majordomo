package page.devnet.translate.yandex;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import page.devnet.common.webclient.WebClientFactory;
import page.devnet.translate.TranslateException;
import page.devnet.translate.TranslateService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author maksim
 * @since 02.03.19
 */
@Slf4j
public class YandexTranslateService implements TranslateService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;
    private final WebClient client;
    private static final String TRANSLATE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";

    public YandexTranslateService(String apiKey) {
        this.apiKey = apiKey;
        this.client = createWebClient();
    }

    protected WebClient createWebClient() {
        return WebClientFactory.createWebClient(Vertx.vertx());
    }

    public String transRuToEn(String text) throws TranslateException {
        Objects.requireNonNull(text);

        if (!isRuText(text)) {
            return "";
        }

        return translate("ru-en", text);
    }

    private static final Pattern cyrillic = Pattern.compile("[А-Яа-я]");

    private boolean isRuText(String text) {
        return cyrillic.matcher(text).find();
    }

    public String transEnToRu(String text) throws TranslateException {
        Objects.requireNonNull(text);

        return translate("en-ru", text);
    }

    private String translate(String lang, String text) throws TranslateException {
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String formData = "text=" + encodedText;

        try {
            HttpResponse<Buffer> response = client.postAbs(TRANSLATE_URL)
                    .addQueryParam("key", apiKey)
                    .addQueryParam("lang", lang)
                    .putHeader("Content-Type", "application/x-www-form-urlencoded")
                    .sendBuffer(Buffer.buffer(formData))
                    .onFailure(e -> {
                        log.error("Translation request failed", e);
                        throw new TranslateException(e, text);
                    })
                    .result();

            if (response.statusCode() != 200) {
                log.error("Translation failed with status code: {}", response.statusCode());
                return "";
            }

            TranslateResponse resp = mapper.readValue(response.bodyAsString(), TranslateResponse.class);
            if (resp.getCode() == 200) {
                return String.join("\n", resp.getText());
            } else {
                log.error("Translation failed with response code: {}", resp.getCode());
            }
        } catch (Exception e) {
            log.error("Translation failed", e);
            throw new TranslateException(e, text);
        }

        return "";
    }

    @Data
    private static class TranslateResponse {
        Integer code;
        String lang;
        List<String> text;
        String message;  // For error responses
    }
}

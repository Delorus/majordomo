package page.devnet.translate.yandex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import page.devnet.translate.TranslateException;
import page.devnet.translate.TranslateService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author maksim
 * @since 02.03.19
 */
@Slf4j
public final class YandexTranslateService implements TranslateService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final URI yandexTranslate;
    private final HttpClient client;

    public YandexTranslateService(String apiKey) {
        try {
            this.yandexTranslate = new URIBuilder(URI.create("https://translate.yandex.net/api/v1.5/tr.json/translate"))
                    .addParameter("key", apiKey)
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new TranslateException(e);
        }

        this.client = HttpClients.createMinimal();
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
        URI uri;
        try {
            uri = new URIBuilder(yandexTranslate)
                    .addParameter("lang", lang)
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new TranslateException(e, text);
        }

        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(new StringEntity("text=" + text, StandardCharsets.UTF_8));

        HttpResponse response = tryExecute(post, text);
        try (InputStreamReader reader = new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)) {
            TranslateResponse resp = mapper.readValue(reader, TranslateResponse.class);
            if (resp.getCode() == 200) {
                return String.join("\n", resp.getText());
            } else {
                log.error(response.toString());
            }
        } catch (IOException e) {
            throw new TranslateException(e, text);
        }

        return "";
    }

    private HttpResponse tryExecute(HttpPost post, String text) throws TranslateException {
        try {
            return client.execute(post);
        } catch (IOException e) {
            throw new TranslateException(e, text);
        }
    }

    @Data
    private static class TranslateResponse {
        Integer code;
        String lang;
        List<String> text;
    }
}
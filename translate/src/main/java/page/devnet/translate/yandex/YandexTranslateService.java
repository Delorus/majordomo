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
import page.devnet.translate.TranslateService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

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
            throw new RuntimeException(e);
        }

        this.client = HttpClients.createMinimal();
    }

    public String transRuToEn(String text) throws IOException {
        Objects.requireNonNull(text);

        return translate("ru-en", text);
    }

    public String transEnToRu(String text) throws IOException {
        Objects.requireNonNull(text);

        return translate("en-ru", text);
    }

    private String translate(String lang, String text) throws IOException {
        URI uri;
        try {
            uri = new URIBuilder(yandexTranslate)
                    .addParameter("lang", lang)
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(new StringEntity("text=" + text, StandardCharsets.UTF_8));

        HttpResponse response = client.execute(post);
        try (InputStreamReader reader = new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)) {
            TranslateResponse resp = mapper.readValue(reader, TranslateResponse.class);
            if (resp.getCode() == 200) {
                return String.join("\n", resp.getText());
            } else {
                log.error(response.toString());
            }
        }

        return "";
    }

    @Data
    private static class TranslateResponse {
        Integer code;
        String lang;
        List<String> text;
    }
}
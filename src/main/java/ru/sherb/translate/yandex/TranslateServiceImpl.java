package ru.sherb.translate.yandex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import ru.sherb.translate.TranslateService;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author maksim
 * @since 02.03.19
 */
public final class TranslateServiceImpl implements TranslateService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final URI yandexTranslate;
    private final HttpClient client;

    public TranslateServiceImpl(String apiKey) {
        this.yandexTranslate = UriBuilder.fromUri("https://translate.yandex.net/api/v1.5/tr.json/translate")
                .queryParam("key", apiKey)
                .build();
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
        URI uri = UriBuilder.fromUri(yandexTranslate)
                .queryParam("lang", lang)
                .build();

        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(new UrlEncodedFormEntity(
                Collections.singletonList(new BasicNameValuePair("text", text))));

        HttpResponse response = client.execute(post);
        try (InputStreamReader reader = new InputStreamReader(response.getEntity().getContent(), Charset.forName("UTF-8"))) {
            TranslateResponse resp = mapper.readValue(reader, TranslateResponse.class);
            if (resp.getCode() == 200) { //todo log it!
                return String.join("\n", resp.getText());
            } else {
                System.out.println("Something wrong:");
                System.out.println(response);
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
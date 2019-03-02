package ru.sherb.translate.yandex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import ru.sherb.translate.TranslateService;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        this.client = HttpClient.newHttpClient();
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

        HttpRequest request = HttpRequest.newBuilder(uri)
                .headers("Content-Type", "application/x-www-form-urlencoded",
                        "Content-Length", String.valueOf("text=".length() + text.length()))
                .POST(HttpRequest.BodyPublishers.ofString("text=" + text))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TranslateResponse resp = mapper.readValue(response.body(), TranslateResponse.class);
            if (resp.getCode() == 200) { //todo log it!
                return String.join("\n", resp.getText());
            } else {
                System.out.println("Something wrong:");
                System.out.println(response);
            }
        } catch (InterruptedException ie) {
            //todo log it!
            ie.printStackTrace();
            Thread.currentThread().interrupt();
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
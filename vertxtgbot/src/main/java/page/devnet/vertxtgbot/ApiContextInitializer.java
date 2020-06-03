package page.devnet.vertxtgbot;

import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.generics.Webhook;

public final class ApiContextInitializer {

    private ApiContextInitializer() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void init() {
        ApiContext.registerSingleton(Webhook.class, VertxWebhook.class);
    }
}

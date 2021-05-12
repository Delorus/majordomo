package page.devnet.vertxtgbot;

import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.Webhook;

public final class ApiContextInitializer {

    private ApiContextInitializer() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void init(boolean isProdEnv) {
        if (isProdEnv) {
            ApiContext.registerSingleton(Webhook.class, VertxWebhook.class);
        } else {
            ApiContext.registerSingleton(BotSession.class, VertxBotSession.class);
        }
    }
}

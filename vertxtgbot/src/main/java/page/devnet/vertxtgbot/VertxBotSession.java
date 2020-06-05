package page.devnet.vertxtgbot;

import io.vertx.core.Vertx;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

/**
 *
 * @author maksim
 * @since 05.06.2020
 */
public class VertxBotSession implements BotSession {

    private final Vertx vertx;

    public VertxBotSession() {

        vertx = GlobalVertxHolder.getVertx();
    }

    @Override
    public void setOptions(BotOptions options) {

    }

    @Override
    public void setToken(String token) {

    }

    @Override
    public void setCallback(LongPollingBot callback) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}

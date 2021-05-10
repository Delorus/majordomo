package page.devnet.vertxtgbot.util;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/**
 * @author maksim
 * @since 03.06.2020
 */
public class ReferenceBot extends DefaultAbsSender {

    public static ReferenceBot newBot(int port) {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setBaseUrl("http://localhost:" + port + "/");

        return new ReferenceBot(options);
    }

    protected ReferenceBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
        return "test_bot";
    }
}

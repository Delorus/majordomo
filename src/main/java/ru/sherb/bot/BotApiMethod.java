package ru.sherb.bot;

/**
 * @author maksim
 * @since 10.11.2019
 */
public class BotApiMethod {

    public static BotApiMethod from(org.telegram.telegrambots.meta.api.methods.BotApiMethod<?> origin) {
        return new BotApiMethod(origin);
    }

    private final org.telegram.telegrambots.meta.api.methods.BotApiMethod<?> origin;

    private BotApiMethod(org.telegram.telegrambots.meta.api.methods.BotApiMethod<?> origin) {
        this.origin = origin;
    }


    public org.telegram.telegrambots.meta.api.methods.BotApiMethod<?> unwrap() {
        return origin;
    }
}

package page.devnet.vertxtgbot.tgapi;

import io.vertx.ext.web.client.WebClient;

/**
 * Интерфейс для объединения всех действий с телеграмом.
 * <p/>
 * Работа с API телеграма представлена в виде паттерна "Команда".
 *
 * @see page.devnet.vertxtgbot.tgapi.TelegramSender
 * @author maksim
 * @since 31.05.2020
 */
interface TelegramAction {

    void execute(WebClient transport);
}

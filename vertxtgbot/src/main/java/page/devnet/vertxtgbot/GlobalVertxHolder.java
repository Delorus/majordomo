package page.devnet.vertxtgbot;

import io.vertx.core.Vertx;

/**
 * Из-за того что приходится использовать DI для регистрации вебхука
 * (см. {@link page.devnet.vertxtgbot.ApiContextInitializer})
 * мы не можем просто так заинжектить Vertx в нужные нам классы.
 * <p/>
 * Этот класс решает эту проблему, просто хранив глобальный синглтон Vertx.
 *
 * @author maksim
 * @since 28.05.2020
 */
public final class GlobalVertxHolder {

    private static final Vertx vertx = Vertx.vertx();

    public static Vertx getVertx() {
        return vertx;
    }

    private GlobalVertxHolder() throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}

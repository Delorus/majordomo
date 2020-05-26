package page.devnet.vertxbot;

import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.generics.Webhook;

public final class ApiContextInitializer {

    private ApiContextInitializer(){
    }

    public static void init(){

        ApiContext.register(Webhook.class, DefaultWebHook.class);
    }
}

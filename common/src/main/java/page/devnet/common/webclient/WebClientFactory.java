package page.devnet.common.webclient;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

/**
 * Factory for creating WebClient instances with proper configuration.
 */
public class WebClientFactory {
    private static final int DEFAULT_TIMEOUT = 5000;

    /**
     * Creates a WebClient instance with default configuration.
     *
     * @param vertx Vertx instance
     * @return configured WebClient
     */
    public static WebClient createWebClient(Vertx vertx) {
        return createWebClient(vertx, DEFAULT_TIMEOUT);
    }

    /**
     * Creates a WebClient instance with custom timeout.
     *
     * @param vertx Vertx instance
     * @param timeout timeout in milliseconds
     * @return configured WebClient
     */
    public static WebClient createWebClient(Vertx vertx, int timeout) {
        WebClientOptions options = new WebClientOptions()
            .setConnectTimeout(timeout)
            .setIdleTimeout(timeout)
            .setKeepAlive(true);
        
        return WebClient.create(vertx, options);
    }
}
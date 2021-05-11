package page.devnet.pluginmanager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author sherb
 * @since 10.05.2021
 */
public final class MultiTenantPluginManager<T, R> implements MessageSubscriber<T, R> {

    // особая группа для stateless-плагинов
    public static final String NO_TENANT = "[stateless]";

    private final Function<String, MessageSubscriber<T, R>> generator;
    private final Function<T, String> tenantIdExtractor;
    private final MessageSubscriber<T, R> statelessManager;

    private final ConcurrentMap<String, MessageSubscriber<T, R>> managers = new ConcurrentHashMap<>();

    public MultiTenantPluginManager(Function<String, MessageSubscriber<T, R>> generator, Function<T, String> tenantIdExtractor, MessageSubscriber<T, R> statelessManager) {
        this.generator = generator;
        this.tenantIdExtractor = tenantIdExtractor;
        this.statelessManager = statelessManager;
    }

    public MultiTenantPluginManager(Function<String, MessageSubscriber<T, R>> generator, Function<T, String> tenantIdExtractor) {
        this(generator, tenantIdExtractor, event -> Collections.emptyList());
    }

    @Override
    public List<R> consume(T event) {
        String id = tenantIdExtractor.apply(event);
        if (NO_TENANT.equals(id)) {
            return statelessManager.consume(event);
        }

        MessageSubscriber<T, R> sub = managers.computeIfAbsent(id, generator);
        return sub.consume(event);
    }
}

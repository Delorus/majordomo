package page.devnet.pluginmanager;

import java.util.List;

/**
 * @author maksim
 * @since 16.11.2019
 */
public interface MessageSubscriber<T, R> {

    List<R> consume(T event);
}

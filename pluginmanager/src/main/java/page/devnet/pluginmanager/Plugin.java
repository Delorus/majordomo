package page.devnet.pluginmanager;

/**
 * @author maksim
 * @since 23.03.19
 */
public interface Plugin<T, R> {

    R onEvent(T event);
}

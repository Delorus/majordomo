package page.devnet.pluginmanager;

/**
 * @author maksim
 * @since 23.03.19
 */
public interface Plugin<T, R> {

    String getPluginId();

    R onEvent(T event);

}

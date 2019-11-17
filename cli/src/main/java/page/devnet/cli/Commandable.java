package page.devnet.cli;

import java.util.Map;

/**
 * @author maksim
 * @since 16.11.2019
 */
public interface Commandable {

    String serviceName();

    Map<String, String> commandDescriptionList();
}

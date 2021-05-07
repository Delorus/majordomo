package page.devnet.telegrambot.util;

import lombok.Value;

@Value
public class ParsedCommand {

    Command command = Command.NONE;
    String text = "";

}

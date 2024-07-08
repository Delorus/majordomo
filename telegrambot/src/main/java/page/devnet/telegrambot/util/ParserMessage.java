package page.devnet.telegrambot.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParserMessage {

    private final CommandUtils commandUtils = new CommandUtils();

    public Command getCommandFromMessage(String message) {
        String messageToUpperCase = commandUtils.normalizeCmdMsg(message).toUpperCase().trim();
        if (messageToUpperCase.contains(" ")) {
            messageToUpperCase = messageToUpperCase.substring(0, messageToUpperCase.indexOf(" "));
        }
        Command command = Command.NONE;
        try {
            command = Command.valueOf(messageToUpperCase);
        } catch (IllegalArgumentException e) {
            log.error("Command is not found {}", e.getMessage());
            return command;
        }
        return command;
    }

    public String getCommandParameterFromMessage(String message) {
        String commandParameter = "";
        int end = message.length();

        if (message.contains(" ")) {
            commandParameter = message.substring(message.indexOf(" ") + 1, end);
        }

        return commandParameter;
    }


}

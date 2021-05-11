package page.devnet.telegrambot.util;

public class ParserMessage {


    public Command getCommandFromMessage(String message) {
        String messageToUpperCase = message.toUpperCase().trim();
        Command command = Command.NONE;
        try {
            command = Command.valueOf(messageToUpperCase);
        } catch (IllegalArgumentException e) {

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

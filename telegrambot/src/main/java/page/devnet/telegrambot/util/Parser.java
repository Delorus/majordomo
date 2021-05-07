package page.devnet.telegrambot.util;

public class Parser {


    public Command getCommandFromMessage(String message){
        String foo = message.toUpperCase().trim();
        Command command = Command.NONE;
        try {
            command = Command.valueOf(foo);
        }catch (IllegalArgumentException e){

        }
        return command;
    }

    public String getCommandParametrFromMessage(String message){
        String commandParametr = "";
        int end = message.length();

        if (message.contains(" ")){
            commandParametr = message.substring(message.indexOf(" ")+1, end);
        }

        return commandParametr;
    }


}

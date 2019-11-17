package page.devnet.cli;

import lombok.extern.slf4j.Slf4j;
import page.devnet.pluginmanager.MessageSubscriber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Интерактивный интерпретатор командной строки, нужен, в основном, для дебага различных плагинов.
 * <br/>
 * Работает в интерактивном режиме, считывая построчно ввод из input и выводя результат в output.
 * Строки, начинающиеся с символа ":" считаются командами, помимо стандартных комманд,
 * есть еще команды которые зависят от каждого плагина.
 * <p/>
 * Стандартные команды: <br/>
 * {@code :help :?}       — Выводит список всех доступных команд с описанием. <br/>
 * {@code :exit :q :quit} — Выход из интерпритатора
 *
 * @author maksim
 * @since 16.11.2019
 */
@Slf4j
public class Interpreter {

    private final MessageSubscriber<Event, String> subscriber;

    private String helpMsg = "Support commands:\n" +
            "\t\t:help :?       — print list of all available commands\n" +
            "\t\t:exit :q :quit — exit from interpreter\n";

    public Interpreter(MessageSubscriber<Event, String> subscriber) {
        this.subscriber = subscriber;
    }

    //todo протекшая абстракция
    public void setCommands(Commandable... helpers) {
        StringBuilder builder = new StringBuilder(helpMsg);
        for (Commandable helper : helpers) {
            builder.append("\t").append(helper.serviceName()).append(":\n");
            int maxLen = maxCmdLength(helper.commandDescriptionList().keySet()) + 1;
            for (Map.Entry<String, String> cmd : helper.commandDescriptionList().entrySet()) {
                builder.append("\t\t").append(cmd.getKey());

                int len = cmd.getKey().length();
                builder.append(offset(maxLen, len)).append("— ")
                        .append(cmd.getValue()).append("\n");
            }
        }

        helpMsg = builder.toString();
    }

    private int maxCmdLength(Set<String> keySet) {
        return keySet.stream()
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElseThrow();
    }

    private String offset(int maxLen, int len) {
        return IntStream.range(len, maxLen)
                .mapToObj(__ -> " ")
                .collect(Collectors.joining());
    }

    public void run(InputStream in, PrintStream out) {
        printHeader(out);

        try (var reader = new BufferedReader(new InputStreamReader(in))) {
            while (true) {
                printCursor(out);
                var line = reader.readLine();

                String result;
                if (isCommand(line)) {
                    if (isStop(line)) {
                        break;
                    }
                    result = executeCommand(line);
                } else {
                    result = sendToSubscriber(line);
                }

                if (!result.isEmpty()) {
                    out.println(result);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            System.err.println(e.getMessage());
        }

        out.println("Goodbye!");
    }

    private void printHeader(PrintStream out) {
        out.println("Welcome to the command line interpreter.\n" +
                "To get all available commands, enter :help.\n" +
                "To exit, enter :quit");
    }

    private void printCursor(PrintStream out) {
        out.print("> ");
    }

    private boolean isCommand(String line) {
        return line.startsWith(":");
    }

    private boolean isStop(String cmd) {
        cmd = cmd.trim();
        return cmd.equals(":quit") || cmd.equals(":exit") || cmd.equals(":q");
    }

    private String executeCommand(String cmd) {
        if (isHelpRequest(cmd)) {
            return helpMessage();
        }

        return String.join("\n", subscriber.consume(new Event(cmd)));
    }

    private boolean isHelpRequest(String cmd) {
        cmd = cmd.trim();
        return cmd.equals(":help") || cmd.equals(":?");
    }

    private String helpMessage() {
        return helpMsg;
    }

    private String sendToSubscriber(String line) {
        return String.join("\n", subscriber.consume(new Event(line)));
    }
}

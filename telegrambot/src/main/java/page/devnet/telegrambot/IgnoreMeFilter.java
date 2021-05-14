package page.devnet.telegrambot;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import page.devnet.database.repository.IgnoreMeRepository;
import page.devnet.pluginmanager.MessageSubscriber;
import page.devnet.telegrambot.util.CommandUtils;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * @author sherb
 * @since 14.05.2021
 */
public class IgnoreMeFilter implements MessageSubscriber<Update, List<PartialBotApiMethod>> {

    private final MessageSubscriber<Update, List<PartialBotApiMethod>> subscriber;
    private final IgnoreMeRepository ignoreMeRepository;
    private final CommandUtils commandUtils = new CommandUtils();

    public IgnoreMeFilter(MessageSubscriber<Update, List<PartialBotApiMethod>> subscriber, IgnoreMeRepository ignoreMeRepository) {
        this.subscriber = subscriber;
        this.ignoreMeRepository = ignoreMeRepository;
    }

    @Override
    public List<List<PartialBotApiMethod>> consume(Update event) {
        if (!event.hasMessage()) {
            return subscriber.consume(event);
        }

        var msg = event.getMessage();
        if (msg.isCommand()) {
            if (isSuitableCmd(msg)) {
                return List.of(execCmd(msg));
            } else {
                return subscriber.consume(event);
            }
        }

        Integer id = msg.getFrom().getId();
        if (ignoreMeRepository.contains(id)) {
            return Collections.emptyList();
        }

        return subscriber.consume(event);
    }

    private boolean isSuitableCmd(Message cmd) {
        switch (commandUtils.normalizeCmdMsg(cmd.getText())) {
            case "ignoremeplease":
                return true;
            case "dontignoreme":
                return true;
            default:
                return false;
        }
    }

    private List<PartialBotApiMethod> execCmd(Message cmd) {
        switch (commandUtils.normalizeCmdMsg(cmd.getText())) {
            case "ignoremeplease":
                var added = ignoreMeRepository.add(cmd.getFrom().getId());
                if (!added) {
                    return List.of(new SendMessage(cmd.getChatId(), i18n("TGEtbGEtbGEsIGkgZG9uJ3QgbGlzdGVuIHRvIHlvdSwgeW91J3JlIGlnbm9yZWQ=")));
                }
                return List.of(new SendMessage(cmd.getChatId(), i18n("QXMgeW91ciB3aXNo")));
            case "dontignoreme":
                var removed = ignoreMeRepository.remove(cmd.getFrom().getId());
                if (!removed) {
                    return List.of(new SendMessage(cmd.getChatId(), i18n("SSBuZXZlciBpZ25vcmVkIHlvdSwgaGVyZSBtdXN0IGJlIHNvbWUgbWlzdGFrZQ==")));
                }
                return List.of(new SendMessage(cmd.getChatId(), i18n("SSB3aWxsIG5vdCBpZ25vcmUgeW91IGFueW1vcmU=")));
            default:
                throw new IllegalStateException("it can't happen");
        }
    }

    private static String i18n(String text) {
        return new String(Base64.getDecoder().decode(text));
    }
}

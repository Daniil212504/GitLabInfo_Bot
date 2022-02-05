package telegramBot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramBot.Bot;
import telegramBot.utils.Property;

import java.util.Arrays;

import static java.util.Objects.*;
import static java.util.stream.Collectors.joining;
import static org.telegram.telegrambots.meta.api.methods.ParseMode.*;

public class ExceptionHandler {
    private static Bot bot;

    public static void setBot(Bot bot) {
        ExceptionHandler.bot = bot;
    }

    public static void handleException(Throwable e) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(Property.get("tg.helpChat"))
                .parseMode(MARKDOWN)
                .text(messageText(e))
                .build();

        try {
            if (nonNull(bot)) {
                bot.execute(sendMessage);
            } else {
                e.printStackTrace();
            }
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    private static String messageText(Throwable e) {
        Throwable cause = e.getCause();
        String message = e.getMessage();
        String stackTrace = Arrays.stream(e.getStackTrace())
                .filter(el -> el.getClassName().contains("telegramBot"))
                .map(StackTraceElement::toString)
                .collect(joining("\n"));

        return "*Cause:* `" + (isNull(cause) ? "java.lang.NullPointerException" : cause.toString()) + "`" +
                (isNull(message) ? "" : "\n\n*Message:* " + message) +
                "\n\n*StackTrace:*\n`" + stackTrace + "`";
    }
}

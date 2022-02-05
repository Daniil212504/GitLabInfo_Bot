package telegramBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramBot.handlers.ExceptionHandler;
import telegramBot.keyBoards.Popups.Buttons;
import telegramBot.model.CustomMessage;

import java.util.Date;

import static java.util.Objects.nonNull;
import static org.telegram.telegrambots.meta.api.methods.ParseMode.HTML;
import static telegramBot.handlers.CommandHandler.applyCommand;
import static telegramBot.keyBoards.Popups.Popup.getInlineKeyBoardMessage;

public class Sender {
    private static Bot bot;

    public static void setBot(Bot bot) {
        Sender.bot = bot;
    }

    public static void sendReaction(String chatId, String stickerId, String messageText) {
        sendReaction(chatId, stickerId, messageText, -1);
    }

    public static void sendReaction(String chatId, String stickerId, String messageText, int mainProjectID) {
        Integer messageId = sendSticker(chatId, stickerId);
        sendMessage(chatId, applyCommand(messageText, mainProjectID));
        if (messageId != 0)
            deleteMessage(chatId, messageId);
    }

    public static void deleteMessage(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();

        try {
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void sendInlineKeyBoard(SendMessage messageKeyboard) {
        try {
            bot.execute(messageKeyboard);
        } catch (TelegramApiException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private static Integer sendSticker(String chatId, String stickerId) {
        SendSticker sendSticker = SendSticker.builder()
                .chatId(chatId)
                .sticker(new InputFile(stickerId))
                .build();

        Integer messageId = 0;
        try {
            messageId = bot.execute(sendSticker).getMessageId();
        } catch (TelegramApiException e) {
            ExceptionHandler.handleException(e);
        }

        return messageId;
    }

    public static void sendMessage(String chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .parseMode(HTML)
                .disableWebPagePreview(true)
                .text(message)
                .build();

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void sendMessageWithSave(String chatId, CustomMessage customMessage, String title) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .parseMode(HTML)
                .disableWebPagePreview(true)
                .text(title + customMessage.getText())
                .build();

        try {
            Message m =  bot.execute(sendMessage);
            customMessage.setChatId_and_MessagesId(chatId, m.getMessageId());
            customMessage.setSentDate(new Date((long)  m.getDate() * 1000));
        } catch (TelegramApiException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void sendMessageWithSave(String chatId, CustomMessage customMessage, String title, Buttons button1, Buttons button2) {
        SendMessage sendMessage = getInlineKeyBoardMessage(chatId, title + customMessage.getText(), button1, button2);
        try {
            Message m =  bot.execute(sendMessage);
            customMessage.setChatId_and_MessagesId(chatId, m.getMessageId());
            customMessage.setSentDate(new Date((long)  m.getDate() * 1000));
        } catch (TelegramApiException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static void editMessage(String chatId, Integer messageId, String newText) {
        editMessage(chatId, messageId, newText, null);
    }

    public static void editMessage(String chatId, Integer messageId, String newText, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .parseMode(HTML)
                .disableWebPagePreview(true)
                .text(newText)
                .build();

        if (nonNull(inlineKeyboardMarkup))
            editMessageText.setReplyMarkup(inlineKeyboardMarkup);

        try {
            bot.execute(editMessageText);
        } catch (TelegramApiException e) {
            ExceptionHandler.handleException(e);
        }
    }

}

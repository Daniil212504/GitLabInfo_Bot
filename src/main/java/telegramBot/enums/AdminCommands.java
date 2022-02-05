package telegramBot.enums;

import telegramBot.keyBoards.Popups.Popup;
import telegramBot.utils.Property;

import static telegramBot.Sender.*;

public enum AdminCommands {
    SEND_TO_ALL("/sendToAll", "Отправить во все доверенные чаты приведенный ниже текст?\n\n"),
    SHUT_DOWN("/shutDown", "Мне завершить работу?");

    private final String commandText;
    private final String title;

    AdminCommands(String commandText, String title) {
        this.commandText = commandText;
        this.title = title;
    }

    public String getCommandText() {
        return commandText;
    }

    public String getTitle() {
        return title;
    }

    public static void applySendToAllCommand(String chatId, Integer messageId, String messageText) {
        deleteMessage(chatId, messageId);
        String text = messageText.replaceAll(SEND_TO_ALL.getCommandText() + "[\n ]*", "");
        sendInlineKeyBoard(Popup.getInlineKeyBoardMessage(Property.get("tg.helpChat"), SEND_TO_ALL.getTitle() + (text.isEmpty() ? "\"\"" : text)));
    }

    public static void applyShutDownCommand(String chatId, Integer messageId) {
        deleteMessage(chatId, messageId);
        sendInlineKeyBoard(Popup.getInlineKeyBoardMessage(Property.get("tg.helpChat"), SHUT_DOWN.getTitle()));
    }
}

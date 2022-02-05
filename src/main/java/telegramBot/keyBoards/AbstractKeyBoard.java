package telegramBot.keyBoards;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.HTML;

public abstract class AbstractKeyBoard {

    protected static InlineKeyboardButton button(String buttonText, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(buttonText)
                .callbackData(callbackData)
                .build();
    }

    protected static SendMessage createSendMessage(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .parseMode(HTML)
                .disableWebPagePreview(true)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }
}

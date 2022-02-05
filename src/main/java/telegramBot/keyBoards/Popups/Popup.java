package telegramBot.keyBoards.Popups;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import telegramBot.keyBoards.AbstractKeyBoard;

import java.util.List;

import static java.util.Arrays.asList;
import static telegramBot.keyBoards.Popups.Buttons.*;

public class Popup extends AbstractKeyBoard {

    public static SendMessage getInlineKeyBoardMessage(String chatId, String text) {
        return getInlineKeyBoardMessage(chatId, text, YES, NO);
    }

    public static SendMessage getInlineKeyBoardMessage(String chatId, String text, Buttons button1, Buttons button2) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(button1, button2);
        return createSendMessage(chatId, text, inlineKeyboardMarkup);
    }

    public static InlineKeyboardMarkup getInlineKeyboardMarkup(Buttons button1, Buttons button2) {
        List<InlineKeyboardButton> row = asList(
                button(button1.getButtonText(), button1.getButtonText()),
                button(button2.getButtonText(), button2.getButtonText())
        );
        return InlineKeyboardMarkup.builder()
                .keyboardRow(row)
                .build();
    }

}

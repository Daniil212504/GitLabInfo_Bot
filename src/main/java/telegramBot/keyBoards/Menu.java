package telegramBot.keyBoards;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static java.util.Arrays.asList;
import static telegramBot.enums.MenuCommands.*;

public class Menu extends AbstractKeyBoard {
    public static String menuCommand = "/menu@GitLabInfo_Bot";

    public static SendMessage getInlineKeyBoardMessage(String chatId) {
        List<InlineKeyboardButton> row_1 = asList(
                button(GET_ALL_REVIEW_MERGE_REQUESTS.getButtonText(), GET_ALL_REVIEW_MERGE_REQUESTS.getCommandText()),
                button(GET_ALL_REVIEW_ISSUES.getButtonText(), GET_ALL_REVIEW_ISSUES.getCommandText())
        );
        List<InlineKeyboardButton> row_2 = asList(
                button(GET_ALL_DOING_MERGE_REQUESTS.getButtonText(), GET_ALL_DOING_MERGE_REQUESTS.getCommandText()),
                button(GET_ALL_DOING_ISSUES.getButtonText(), GET_ALL_DOING_ISSUES.getCommandText())
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(asList(row_1, row_2))
                .build();

        return createSendMessage(chatId, "Menu", inlineKeyboardMarkup);
    }

}

package telegramBot.handlers;


import org.telegram.telegrambots.meta.api.objects.Message;
import telegramBot.enums.MainProjects;
import telegramBot.keyBoards.Menu;
import telegramBot.enums.MenuCommands;
import telegramBot.enums.NotificationCommands;
import telegramBot.model.CustomMessage;

import java.util.*;

import static telegramBot.enums.AdminCommands.*;
import static telegramBot.keyBoards.Menu.menuCommand;
import static telegramBot.Sender.*;

public class CommandHandler extends Handler {

    public static void handleCommand(Message message) {
        String messageText = message.getText();
        Integer messageId = message.getMessageId();
        String chatId = String.valueOf(message.getChatId());
        boolean isMenuCommand = messageText.equals(menuCommand) || messageText.equals(menuCommand.substring(0, menuCommand.indexOf("@")));
        boolean isSendToAllCommand = messageText.startsWith(SEND_TO_ALL.getCommandText()) && chatId.equals(helpChatId);
        boolean isShutDownCommand = messageText.equals(SHUT_DOWN.getCommandText()) && chatId.equals(helpChatId);

        if (isMenuCommand) {
            applyMenuCommand(chatId, messageId);
        } else if (isSendToAllCommand) {
            applySendToAllCommand(chatId, messageId, messageText);
        } else if (isShutDownCommand) {
            applyShutDownCommand(chatId, messageId);
        } else {
            int projectId = getProjectId(chatId);
            boolean isProjectChat = projectId != -1;
            boolean isMainChat = chatId.equals(mainChatId);

            if (isMainChat) {
                sendReaction(mainChatId, stickerUpload, messageText);
            } else if (isProjectChat) {
                sendReaction(chatId, stickerUpload, messageText, projectId);
            }
        }
    }

    private static void applyMenuCommand(String chatId, Integer messageId) {
        boolean isTrustedChat = Arrays.stream(MainProjects.values())
                .map(MainProjects::getChatId)
                .filter(mP_chat -> !mP_chat.isEmpty())
                .anyMatch(mP_chat -> mP_chat.equals(chatId)) || chatId.equals(mainChatId) || chatId.equals(helpChatId);

        deleteMessage(chatId, messageId);
        if (isTrustedChat) {
            sendInlineKeyBoard(Menu.getInlineKeyBoardMessage(chatId));
        } else {
            sendMessage(chatId, "Я приватный бот, могу отправлять данные только в доверенные чаты");
        }
    }

    public static String applyCommand(String command) {
        return applyCommand(command, -1);
    }

    public static String applyCommand(String command, int projectId) {
        String result = "У меня нет команды " + command;
        for (MenuCommands c : MenuCommands.values()) {
            if (c.getCommandText().equals(command))
                result = c.execute(projectId);
        }

        return result;
    }

    public static List<CustomMessage> notificationCommand(NotificationCommands command) {
        return notificationCommand(command, command.getDateAfter());
    }

    public static List<CustomMessage> notificationCommand(NotificationCommands command, Date afterDate) {
        return command.execute(afterDate);
    }
}

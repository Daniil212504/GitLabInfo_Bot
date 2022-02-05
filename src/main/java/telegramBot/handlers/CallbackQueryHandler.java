package telegramBot.handlers;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import telegramBot.keyBoards.Popups.Buttons;
import telegramBot.utils.Property;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static telegramBot.Sender.*;
import static telegramBot.enums.AdminCommands.*;
import static telegramBot.keyBoards.Popups.PopupsCallBack.*;

public class CallbackQueryHandler extends Handler {

    public static void handleCallBackQuery(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String data = callbackQuery.getData();
        String chatId = String.valueOf(message.getChatId());
        int projectId = getProjectId(chatId);
        boolean isProjectChat = projectId != -1;
        boolean isMainChat = chatId.equals(mainChatId);
        boolean isHelpChat = chatId.equals(Property.get("tg.helpChat"));

        if (isMainChat) {
            Buttons approveOrCommented = getButton(data);
            if (nonNull(approveOrCommented)) {
                approvedOrCommentedCallBack(callbackQuery, approveOrCommented);
            } else {
                deleteMessage(mainChatId, message.getMessageId());
                sendReaction(mainChatId, stickerUpload, data);
            }
        } else if (isProjectChat) {
            deleteMessage(chatId, message.getMessageId());
            sendReaction(chatId, stickerUpload, data, projectId);
        } else if (isHelpChat) {
            Buttons yesOrNo = getButton(data);
            if (nonNull(yesOrNo)) {
                popupCallBack(message, yesOrNo);
            } else {
                deleteMessage(chatId, message.getMessageId());
                sendMessage(chatId, "В группу поддержки я не могу отправить данные из Gitlab");
            }
        }
    }

    private static void popupCallBack(Message message, Buttons button) {
        deleteMessage(helpChatId, message.getMessageId());
        String messageText = message.getText();

        if (messageText.startsWith(SEND_TO_ALL.getTitle())) {
            applyYesOrNo_forSendToAll(messageText, button);
        } else if (messageText.startsWith(SHUT_DOWN.getTitle())) {
            applyYesOrNoCallBack_forShutDown(button);
        } else {
            sendMessage(helpChatId, "Действие для клавиатуры с текстом \"" + messageText + "\" не определено!");
        }
    }

    private static void approvedOrCommentedCallBack(CallbackQuery callbackQuery, Buttons approveOrCommented) {
        Message message = callbackQuery.getMessage();
        String url = callbackQuery.getMessage().getEntities().stream().map(MessageEntity::getUrl).filter(Objects::nonNull).collect(joining());
        String textWithUrl = Arrays.stream(message.getText().split("\n"))
                .map(t -> t.contains("Title: ") ? "Title: <a href=\"" + url + "\">" + t.replace("Title: ", "") + "</a>" : t)
                .collect(joining("\n"));

        String userFirstName = callbackQuery.getFrom().getFirstName();
        String userLastName = callbackQuery.getFrom().getLastName();
        String user = ((isNull(userFirstName) ? "" : userFirstName) + (isNull(userLastName) ? "" : " " + userLastName.charAt(0) + ".")).trim();

        applyApprovedOrCommented(approveOrCommented, textWithUrl, message.getMessageId(), user);
    }

    private static Buttons getButton(String data) {
        return Arrays.stream(Buttons.values()).filter(b -> b.getButtonText().equals(data)).findAny().orElse(null);
    }
}

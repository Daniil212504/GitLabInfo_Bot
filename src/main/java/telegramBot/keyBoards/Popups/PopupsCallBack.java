package telegramBot.keyBoards.Popups;

import telegramBot.enums.MainProjects;
import telegramBot.jobs.RToRMr_History;
import telegramBot.model.CustomMessage;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static telegramBot.Sender.editMessage;
import static telegramBot.Sender.sendMessage;
import static telegramBot.enums.AdminCommands.SEND_TO_ALL;
import static telegramBot.handlers.Handler.helpChatId;
import static telegramBot.handlers.Handler.mainChatId;
import static telegramBot.keyBoards.Popups.Buttons.APPROVED;
import static telegramBot.keyBoards.Popups.Buttons.COMMENTED;
import static telegramBot.keyBoards.Popups.Popup.getInlineKeyboardMarkup;
import static telegramBot.utils.Helper.bold;
import static telegramBot.utils.Helper.regexFrom;

public class PopupsCallBack {

    public static void applyYesOrNo_forSendToAll(String messageText, Buttons button) {
        String text = messageText.replace(SEND_TO_ALL.getTitle(), "");
        switch (button) {
            case YES:
                sendMessage(mainChatId, text);
                Arrays.stream(MainProjects.values())
                        .filter(mP -> !mP.getChatId().isEmpty())
                        .forEach(mP -> sendMessage(mP.getChatId(), text));
                sendMessage(helpChatId, "Отправка сообщения \"" + text + "\" во все доверенные чаты выполнена!");
                break;
            case NO:
                sendMessage(helpChatId, "Отправка сообщения \"" + text + "\" во все доверенные чаты отменена!");
                break;
            default:
                sendMessage(helpChatId, "Действие для кнопки \"" + button.getButtonText() + "\" не определено!");
                break;
        }
    }

    public static void applyYesOrNoCallBack_forShutDown(Buttons button) {
        switch (button) {
            case YES:
                sendMessage(helpChatId, "Работа завершена!");
                System.exit(0);
                break;
            case NO:
                sendMessage(helpChatId, "Завершение работы отменено!");
                break;
            default:
                sendMessage(helpChatId, "Действие для кнопки \"" + button.getButtonText() + "\" не определено!");
                break;
        }
    }

    public static void applyApprovedOrCommented(Buttons button, String messageText, Integer messageId, String user) {
        String projectName = messageText.substring(messageText.indexOf("❰"), messageText.indexOf("❱"));
        String text = messageText
                .replaceAll(button.getText() + ".+\n*", "")
                .replace("Merge Request", bold("Merge Request"))
                .replace(projectName, bold(projectName));
        String approvedOrCommented = newApprovedOrCommentedText(messageText, messageId, user, button);

        switch (button) {
            case APPROVED:
                String commentedText = getApprovedOrCommentedText(messageText, COMMENTED);
                text = text.replace(commentedText, "");
                String approved = approvedOrCommented.isEmpty() ? approvedOrCommented : approvedOrCommented + "\n";
                String newText = (text.endsWith("\n\n") ? text : text + "\n\n") + (commentedText.isEmpty()
                        ? approved
                        : approved +
                        commentedText.replace(COMMENTED.getText(), bold(COMMENTED.getText())));

                editMessage(mainChatId, messageId, newText, getInlineKeyboardMarkup(APPROVED, COMMENTED));
                break;
            case COMMENTED:
                String approvedText = getApprovedOrCommentedText(messageText, APPROVED);
                text = !approvedText.isEmpty() ? text.replaceAll("\n\n" + regexFrom(approvedText) + "\n?", "") : text;
                String commented = approvedOrCommented.isEmpty() ? approvedOrCommented : approvedOrCommented;
                newText = (text.endsWith("\n\n") ? text : text + "\n\n") + (approvedText.isEmpty()
                        ? commented
                        : approvedText.replace(APPROVED.getText(), bold(APPROVED.getText())) +
                        "\n" + commented);

                editMessage(mainChatId, messageId, newText, getInlineKeyboardMarkup(APPROVED, COMMENTED));
                break;
            default:
                sendMessage(helpChatId, "Действие для кнопки \"" + button.getText() + "\" не определено!");
                break;
        }
    }

    private static String newApprovedOrCommentedText(String messageText, Integer messageId, String user, Buttons button) {
        String text = Arrays.stream(messageText.split("\n")).filter(t -> t.contains(button.getText())).collect(joining());
        if (text.contains(user)) {
            text = text.replaceAll(",? " + regexFrom(user), "")
                    .replace(":, ", ": ");
            editHistoryRToR(messageId, user, button, true);
        } else {
            text = text.isEmpty()
                    ? button.getText() + user
                    : text + ", " + user;
            editHistoryRToR(messageId, user, button, false);
        }
        return text.equals(button.getText().trim()) ? "" : text.replace(button.getText(), bold(button.getText()));
    }

    private static void editHistoryRToR(Integer messageId, String user, Buttons button, boolean containsUser) {
        List<CustomMessage> messagesForEditing = RToRMr_History.history.stream()
                .filter(cM -> cM.getChatId_and_MessagesId().entrySet().stream()
                        .anyMatch(e -> e.getKey().equals(mainChatId) && e.getValue().equals(messageId)))
                .collect(toList());

        if (containsUser) {
            messagesForEditing.forEach(mE -> {
                if (button.equals(APPROVED))
                    mE.getLikesFromUsers().remove(user);
                if (button.equals(COMMENTED))
                    mE.getCommentedFromUsers().remove(user);
            });
        } else {
            messagesForEditing.forEach(mE -> {
                if (button.equals(APPROVED))
                    mE.setLikeFromUser(user);
                if (button.equals(COMMENTED))
                    mE.setCommentedFromUsers(user);
            });
        }
    }

    private static String getApprovedOrCommentedText(String messageText, Buttons button) {
        return Arrays.stream(messageText.split("\n"))
                .filter(t -> t.contains(button.getText()))
                .collect(joining());
    }
}

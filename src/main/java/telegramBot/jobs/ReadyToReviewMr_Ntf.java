package telegramBot.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import telegramBot.Sender;
import telegramBot.enums.MainProjects;
import telegramBot.handlers.ExceptionHandler;
import telegramBot.keyBoards.Popups.Buttons;
import telegramBot.model.CustomMessage;
import telegramBot.utils.Property;

import java.util.*;

import static java.lang.Boolean.parseBoolean;
import static telegramBot.Sender.*;
import static telegramBot.enums.Labels.STATE_REVIEW;
import static telegramBot.enums.NotificationCommands.*;
import static telegramBot.handlers.CommandHandler.notificationCommand;
import static telegramBot.keyBoards.Popups.Buttons.*;
import static telegramBot.keyBoards.Popups.Buttons.COMMENTED;
import static telegramBot.keyBoards.Popups.Popup.getInlineKeyboardMarkup;
import static telegramBot.messageBuilders.Messages.messageHeader;
import static telegramBot.utils.Helper.bold;
import static telegramBot.utils.Helper.dateAfter;

public class ReadyToReviewMr_Ntf extends RToRMr_History implements Job {
    public static final String titleNtf = "#notification\n\n" + messageHeader("\uD83D\uDD34 Merge Request", STATE_REVIEW);
    private static boolean enabledMainProjects;

    public void execute(JobExecutionContext context) {
        try {
            clear_ntfHistory_ReadyToReviewMr();

            JobDataMap jobData = context.getMergedJobDataMap();
            Date dateAfter = jobData.containsKey("afterDate")
                    ? dateAfter((String) jobData.get("afterDate"))
                    : READY_TO_REVIEW_MR.getDateAfter();
            enabledMainProjects = jobData.getBooleanValue("enabledMainProjects");

            List<CustomMessage> reviewMergeRequests = eliminateDuplication(notificationCommand(READY_TO_REVIEW_MR, dateAfter));

            if (!reviewMergeRequests.isEmpty()) {
                List<CustomMessage> new_mR_or_I = getNew_mR_or_I(reviewMergeRequests);
                List<CustomMessage> changedOld_mR_or_I = getChangedOld_mR_or_I(reviewMergeRequests);

                sendNew_mR_or_I(new_mR_or_I, true);
                editMessagesWith_changedOld_mR_or_I(changedOld_mR_or_I, true);

                if (jobData.getBooleanValue("enabledMainProjects")) {
                    sendNew_mR_or_I(new_mR_or_I, false);
                    editMessagesWith_changedOld_mR_or_I(changedOld_mR_or_I, false);
                }

                history.addAll(new_mR_or_I);
            }
        } catch (Throwable e) {
            ExceptionHandler.handleException(e);
        }
    }

    private static void editMessagesWith_changedOld_mR_or_I(List<CustomMessage> changedOld_mR_or_I, boolean inMainChat) {
        if (inMainChat) {
            changedOld_mR_or_I.forEach(mR_or_I -> mR_or_I.getChatId_and_MessagesId()
                    .forEach((chatId, messageId) -> {
                        if (chatId.equals(Property.get("tg.mainChatId"))) {
                            if (parseBoolean(Property.get("ntf.reviewWithButtons.forMainChat"))) {
                                String approved = getApprovedOrCommentedText(mR_or_I.getLikesFromUsers(), APPROVED);
                                String commented = getApprovedOrCommentedText(mR_or_I.getCommentedFromUsers(), COMMENTED);
                                String approvedAndCommented = approved.isEmpty() ? commented : approved + "\n" + commented;
                                String newText = titleNtf + mR_or_I.getText() + (approvedAndCommented.equals("\n") ? "" : "\n\n" + approvedAndCommented);

                                Sender.editMessage(chatId, messageId, newText.endsWith("\n") ? newText.substring(0, newText.length() - 1) : newText, getInlineKeyboardMarkup(APPROVED, COMMENTED));
                            } else {
                                Sender.editMessage(chatId, messageId, titleNtf + mR_or_I.getText());
                            }
                        }
                    })); //редактирование старых mR_or_I в mainChat
        } else {
            if (enabledMainProjects) {
                changedOld_mR_or_I.forEach(mR_or_I -> mR_or_I.getChatId_and_MessagesId()
                        .forEach((chatId, messageId) -> {
                            if (!chatId.equals(Property.get("tg.mainChatId")))
                                editMessage(chatId, messageId, titleNtf + mR_or_I.getText());
                        })); //редактирование старых mR_or_I в проектных чатах
            }
        }
    }

    private static void sendNew_mR_or_I(List<CustomMessage> new_mR_or_I, boolean inMainChat) {
        if (inMainChat) {
            String mainChatId = Property.get("tg.mainChatId");
            if (parseBoolean(Property.get("ntf.reviewWithButtons.forMainChat"))) {
                new_mR_or_I.forEach(mR_or_I -> sendMessageWithSave(mainChatId, mR_or_I, titleNtf, APPROVED, COMMENTED));
            } else {
                new_mR_or_I.forEach(mR_or_I -> sendMessageWithSave(mainChatId, mR_or_I, titleNtf));
            }
        } else {
            if (enabledMainProjects) {
                new_mR_or_I.stream()
                        .filter(p -> Arrays.stream(MainProjects.values()).anyMatch(mP -> mP.getProjectId() == p.getProjectId())) //оставляем MainProjects
                        .forEach(p -> Arrays.stream(MainProjects.values()).forEach(mP -> {
                            if (mP.getProjectId() == p.getProjectId())
                                sendMessageWithSave(mP.getChatId(), p, titleNtf);
                        }));
            }
        }
    }

    private static String getApprovedOrCommentedText(Set<String> approvedOrCommentedUsers, Buttons button) {
        return approvedOrCommentedUsers.isEmpty()
                ? ""
                : bold(button.getText()) +
                approvedOrCommentedUsers.toString().replaceAll("[\\[\\]]", "");
    }

}

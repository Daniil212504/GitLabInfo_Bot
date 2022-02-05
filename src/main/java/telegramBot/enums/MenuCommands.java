package telegramBot.enums;

import static telegramBot.enums.Labels.STATE_DOING;
import static telegramBot.enums.Labels.STATE_REVIEW;
import static telegramBot.getters.GettersForIssues.*;
import static telegramBot.getters.GettersForMergeRequests.*;
import static telegramBot.messageBuilders.Messages.messageHeader;

public enum MenuCommands {
    GET_ALL_REVIEW_MERGE_REQUESTS("\uD83D\uDD34 Review_MR", "/getAllReview_MR"),
    GET_ALL_DOING_MERGE_REQUESTS("\uD83D\uDFE2 Doing_MR", "/getAllDoing_MR"),
    GET_ALL_REVIEW_ISSUES("\uD83D\uDD34 Review_I", "/getAllReview_I"),
    GET_ALL_DOING_ISSUES("\uD83D\uDFE2 Doing_I", "/getAllDoing_I");

    private final String commandText;
    private final String buttonText;

    MenuCommands(String buttonText, String commandText) {
        this.commandText = commandText;
        this.buttonText = buttonText;
    }

    public String getCommandText() {
        return commandText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String execute(int projectId) {
        String result;
        switch (this) {
            case GET_ALL_DOING_ISSUES:
                result = messageHeader("\uD83D\uDFE2 Issues", STATE_DOING) + getAllIssues(STATE_DOING, projectId);
                break;
            case GET_ALL_REVIEW_ISSUES:
                result = messageHeader("\uD83D\uDD34 Issues", STATE_REVIEW) + getAllIssues(STATE_REVIEW, projectId);
                break;
            case GET_ALL_DOING_MERGE_REQUESTS:
                result = messageHeader("\uD83D\uDFE2 Merge Requests", STATE_DOING) + getAllMergeRequests(STATE_DOING, projectId);
                break;
            case GET_ALL_REVIEW_MERGE_REQUESTS:
                result = messageHeader("\uD83D\uDD34 Merge Requests", STATE_REVIEW) + getAllMergeRequests(STATE_REVIEW, projectId);
                break;
            default:
                result = "У меня не описаны действия для команды " + this.getCommandText() + " &#128542;";
                break;
        }

        return result;
    }

}

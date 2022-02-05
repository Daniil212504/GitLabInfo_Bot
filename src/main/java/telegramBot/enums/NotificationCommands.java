package telegramBot.enums;

import telegramBot.model.CustomMessage;
import telegramBot.utils.Property;

import java.util.*;
import java.util.function.Supplier;

import static telegramBot.Sender.sendMessage;
import static telegramBot.enums.Labels.STATE_REVIEW;
import static telegramBot.getters.GettersForIssues.getAllReviewIssues;
import static telegramBot.getters.GettersForMergeRequests.getAllReviewMergeRequests;
import static telegramBot.utils.Helper.dateAfter;
import static telegramBot.utils.Property.*;

public enum NotificationCommands {
    READY_TO_REVIEW_MR("/ready_to_review_MR", () -> dateAfter(get("ntf.reviewMR_lastMinutes"))),
    READY_TO_REVIEW_I("/ready_to_review_I", () -> dateAfter(get("ntf.reviewI_lastMinutes")));

    private final String value;
    private final Supplier<Date> supplierDateAfter;

    NotificationCommands(String value, Supplier<Date> supplierDateAfter) {
        this.value = value;
        this.supplierDateAfter = supplierDateAfter;
    }

    public String getValue() {
        return value;
    }

    public Date getDateAfter() {
        return supplierDateAfter.get();
    }

    public List<CustomMessage> execute() {
        return execute(this.getDateAfter());
    }

    public List<CustomMessage> execute(Date afterDate) {
        List<CustomMessage> result;

        switch (this) {
            case READY_TO_REVIEW_MR:
                result = getAllReviewMergeRequests(afterDate, STATE_REVIEW);
                break;
            case READY_TO_REVIEW_I:
                result = getAllReviewIssues(afterDate, STATE_REVIEW);
                break;
            default:
                result = emptyResult();
                break;
        }

        return result;
    }

    private List<CustomMessage> emptyResult() {
        List<CustomMessage> result = new ArrayList<>();
        CustomMessage customMessage = new CustomMessage(-1, "У меня не описаны действия для команды " + this.getValue() + " &#128542;");

        result.add(customMessage);
        sendMessage(Property.get("tg.helpChat"), customMessage.getText());

        return result;
    }

}

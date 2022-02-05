package telegramBot.jobs;

import telegramBot.model.CustomMessage;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.time.Instant.ofEpochMilli;
import static java.util.stream.Collectors.toList;

public class RToRMr_History {
    public static final List<CustomMessage> history = new ArrayList<>();

    protected static List<CustomMessage> getNew_mR_or_I(List<CustomMessage> reviewMergeRequests) {
        return reviewMergeRequests.stream()
                .filter(mR_or_I -> history.isEmpty() || history.stream()
                        .noneMatch(mPFromHistory -> isOld(mR_or_I.getText(), mPFromHistory.getText()))) //оставляем новые
                .collect(toList());
    }

    protected static List<CustomMessage> getChangedOld_mR_or_I(List<CustomMessage> reviewMergeRequests) {
        return history.stream()
                .filter(mPFromHistory -> reviewMergeRequests.stream()
                        .anyMatch(mR_or_I -> {
                            boolean isChanged = isChanged(mR_or_I.getText(), mPFromHistory.getText());
                            if (isChanged)
                                mPFromHistory.setText(mR_or_I.getText());
                            return isChanged;
                        })) //оставляем старые, но измененные
                .collect(toList());
    }

    protected static List<CustomMessage> eliminateDuplication(List<CustomMessage> reviewMR_or_I) {
        return reviewMR_or_I.stream()
                .filter(mP -> history.stream().noneMatch(mPFromHistory -> mPFromHistory.getText().equals(mP.getText())))
                .collect(toList());
    }

    protected static void clear_ntfHistory_ReadyToReviewMr() {
        List<CustomMessage> oldMessages = history.stream()
                .filter(cM -> Duration.between(ofEpochMilli(cM.getSentDate().getTime()), Instant.now()).toMillis() >= 86400000 * 5 /*5 дней*/)
                .collect(toList());
        oldMessages.forEach(history::remove);
    }

    private static boolean isOld(String newText, String oldText) {
        return deleteLabelsAndLikes(oldText).equals(deleteLabelsAndLikes(newText));
    }

    private static boolean isChanged(String newText, String oldText) {
        LinkedList<String> dldParagraphs = parse(oldText);
        LinkedList<String> newParagraphs = parse(newText);
        boolean equalsTitles = dldParagraphs.get(0).equals(newParagraphs.get(0));
        boolean notEqualsLabels = !dldParagraphs.get(1).equals(newParagraphs.get(1));
        boolean notEqualsLikes = !dldParagraphs.get(2).equals(newParagraphs.get(2));
        return  equalsTitles && (notEqualsLabels || notEqualsLikes);
    }

    private static String deleteLabelsAndLikes(String text) {
        return text
                .replaceAll("\nLabels: \\S+", "")
                .replaceAll("\n\uD83D\uDC4D \\d+", "");
    }

    private static LinkedList<String> parse(String text) {
        LinkedList<String> result = new LinkedList<>();
        int labelsIndex = text.indexOf("\nLabels:");
        int likesIndex = text.indexOf("\n\uD83D\uDC4D");
        boolean labelsExist = labelsIndex != -1;
        boolean likesExist = likesIndex != -1;

        String title = (labelsExist || likesExist) ? text.substring(0, labelsExist ? labelsIndex : likesIndex) : text;
        String labels = labelsExist ? text.substring(labelsIndex, likesExist ? likesIndex : text.length()) : "";
        String likes = likesExist ? text.substring(likesIndex) : "";

        result.add(title);
        result.add(labels);
        result.add(likes);

        return result;
    }

}

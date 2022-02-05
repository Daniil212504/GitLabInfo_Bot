package telegramBot.messageBuilders;

import telegramBot.enums.Labels;
import telegramBot.model.CustomProject;
import telegramBot.model.CustomMessage;
import telegramBot.utils.Helper;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static telegramBot.utils.Helper.bold;

public class MessagesByMergeRequest extends Messages {

    public static List<CustomMessage> findMergeRequests(List<CustomProject> customProjects, Labels label) {
        List<CustomMessage> result = new ArrayList<>();

        customProjects.forEach(p -> {
            Map<Integer, String> mergeRequests = getMergeRequests(p, label);
            Integer projectId = p.getProject().getId();
            String projectName = p.getProject().getName();
            String projectNameTitle = bold("❰ " + projectName + " ❱\n");

            mergeRequests.forEach((mR_ID, mR) -> result.add(new CustomMessage(projectId, projectNameTitle + mR)));
        });

        return result;
    }

    public static String newMergeRequestsMessage(List<CustomProject> customProjects, Labels label) {
        List<String> result = customProjects.stream()
                .map(p -> {
                    Collection<String> mergeRequests = getMergeRequests(p, label).values();
                    return mergeRequests.isEmpty()
                            ? ""
                            : bold("❰ " + p.getProject().getName() + " ❱") + Helper.toString(mergeRequests);
                })
                .filter(mR -> !mR.isEmpty())
                .collect(toList());

        return result.isEmpty() ? "Отсутствуют &#128522" : String.join("\n\n", result);
    }

    private static Map<Integer, String> getMergeRequests(CustomProject project, Labels label) {
        Map<Integer, String> mergeRequestsToMessage = new HashMap<>();

        project.getMergeRequests().forEach(mR -> {
            String titleWithUrl = titleWithUrl(mR.getWebUrl(), mR.getTitle());
            String labels = labelsToString(mR.getLabels().stream()
                    .filter(l -> !l.equals(label.getValue()))
                    .collect(toList()));
            int likesCount = mR.getUpvotes();
            String likes = likesCount != 0 ? "\n\uD83D\uDC4D " + likesCount : "";

            mergeRequestsToMessage.put(mR.getId(), titleWithUrl + labels + likes);
        });

        return mergeRequestsToMessage;
    }

}
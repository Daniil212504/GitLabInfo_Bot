package telegramBot.messageBuilders;

import telegramBot.enums.Labels;
import telegramBot.model.CustomProject;
import telegramBot.model.CustomMessage;
import telegramBot.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static telegramBot.utils.Helper.bold;

public class MessagesByIssues extends Messages {

    public static List<CustomMessage> findIssues(List<CustomProject> customProjects, Labels label) {
        List<CustomMessage> result = new ArrayList<>();

        customProjects.forEach(p -> {
            List<String> issues = getIssues(p, label);
            Integer projectId = p.getProject().getId();
            String projectName = bold("❰ " + p.getProject().getName() + " ❱\n");

            issues.forEach(i -> result.add(new CustomMessage(projectId, projectName + i)));
        });

        return result;
    }

    public static String newIssuesMessage(List<CustomProject> customProjects, Labels label) {
        List<String> result = customProjects.stream()
                .map(p -> {
                    List<String> issues = getIssues(p, label);
                    return issues.isEmpty()
                            ? ""
                            : bold("❰ " + p.getProject().getName() + " ❱") + Helper.toString(issues);
                })
                .filter(mR -> !mR.isEmpty())
                .collect(toList());

        return result.isEmpty() ? "Отсутствуют &#128522" : String.join("\n\n", result);
    }


    private static List<String> getIssues(CustomProject project, Labels label) {
        return project.getIssues().stream()
                .map(i -> {
                    String titleWithUrl = titleWithUrl(i.getWebUrl(), i.getTitle());
                    String labels = labelsToString(i.getLabels().stream()
                            .filter(l -> !l.equals(label.getValue())).collect(toList()));
                    return titleWithUrl + labels;
                })
                .collect(toList());
    }

}

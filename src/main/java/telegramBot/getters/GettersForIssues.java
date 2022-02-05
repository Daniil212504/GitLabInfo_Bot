package telegramBot.getters;

import telegramBot.enums.Labels;
import telegramBot.model.CustomProject;
import org.gitlab4j.api.Constants.IssueState;
import telegramBot.model.CustomMessage;
import telegramBot.readers.ProjectsDataReader;
import telegramBot.utils.Property;

import java.util.Date;
import java.util.List;

import static telegramBot.enums.Action.UPDATED_ISSUE;
import static telegramBot.enums.Labels.STATE_REVIEW;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static telegramBot.messageBuilders.MessagesByIssues.findIssues;
import static telegramBot.messageBuilders.MessagesByIssues.newIssuesMessage;
import static org.gitlab4j.api.Constants.IssueState.OPENED;
import static telegramBot.utils.Helper.dateAfter;


public class GettersForIssues {

    public static List<CustomMessage> getAllReviewIssues(Date afterDate, Labels label) {
        List<CustomProject> projects = getProjects(singletonList(STATE_REVIEW), OPENED, -1, afterDate);

        return findIssues(projects, label);
    }

    public static String getAllIssues(Labels label, int projectId) {
        List<CustomProject> projects = getProjects(singletonList(label), OPENED, projectId, null);

        return newIssuesMessage(projects, label);
    }

    private static List<CustomProject> getProjects(List<Labels> labels, IssueState state, int projectId, Date afterDate) {
        ProjectsDataReader.Builder builder = ProjectsDataReader.builder()
                .labels(labels)
                .state(state)
                .dateAfter(nonNull(afterDate) ? afterDate : dateAfter(Property.get("gl.mR_and_I_lastMinutes")), UPDATED_ISSUE)
                .project(projectId); //если -1, то все из группы gl.projectsGroupId

        return builder.build().getProjects();
    }
}

package telegramBot.readers;

import telegramBot.enums.Action;
import telegramBot.enums.Labels;
import telegramBot.handlers.ExceptionHandler;
import telegramBot.model.CustomProject;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class ProjectsDataReader extends ProjectsReader {
    private final List<CustomProject> customProjects = new ArrayList<>();
    private final IssuesApi issuesApi;
    private final MergeRequestApi mergeRequestApi;

    private ProjectsDataReader(IssueFilter issueFilter, MergeRequestFilter mergeRequestFilter, int projectId) {
        super(projectId);

        issuesApi = gitLabApi.getIssuesApi();
        mergeRequestApi = gitLabApi.getMergeRequestApi();

        read(issueFilter, mergeRequestFilter);
    }

    private void read(IssueFilter issueFilter, MergeRequestFilter mergeRequestFilter) {
        projects.forEach(p -> {
            List<Issue> issues = new ArrayList<>();
            List<MergeRequest> mergeRequests = new ArrayList<>();

            try {
                if (nonNull(issueFilter))
                    issues = getIssues(p, issueFilter);
                if (nonNull(mergeRequestFilter))
                    mergeRequests = getMergeRequests(p, mergeRequestFilter);
            } catch (GitLabApiException e) {
                ExceptionHandler.handleException(e);
            }

            customProjects.add(new CustomProject(p, issues, mergeRequests));
        });
    }

    private List<Issue> getIssues(Project project, IssueFilter issueFilter) throws GitLabApiException {
        return issuesApi.getIssues(project.getId(), isNull(issueFilter) ? new IssueFilter() : issueFilter);
    }

    private List<MergeRequest> getMergeRequests(Project project, MergeRequestFilter mergeRequestFilter) throws GitLabApiException {
        MergeRequestFilter mergeRequestF = isNull(mergeRequestFilter) ? new MergeRequestFilter() : mergeRequestFilter;
        return mergeRequestApi.getMergeRequests(mergeRequestF.withProjectId(project.getId()));
    }

    public List<CustomProject> getProjects() {
        return customProjects;
    }

    public static Builder builder() {
        return new ProjectsDataReader.Builder();
    }


    public static class Builder {
        private IssueFilter issueFilter;
        private MergeRequestFilter mergeRequestFilter;
        private int projectId = -1;

        private Builder() {
            issueFilter = new IssueFilter();
            mergeRequestFilter = new MergeRequestFilter();
        }

        public Builder dateAfter(Date date, Action action) {
            switch (action) {
                case CREATED_ISSUE:
                    issueFilter.setCreatedAfter(date);
                    break;
                case UPDATED_ISSUE:
                    issueFilter.setUpdatedAfter(date);
                    break;
                case CREATED_MERGE_REQUEST:
                    mergeRequestFilter.setCreatedAfter(date);
                    break;
                case UPDATED_MERGE_REQUEST:
                    mergeRequestFilter.setUpdatedAfter(date);
                    break;
                default:
                    try {
                        throw new Exception("Не описаны действия для \"" + action.name() + "\"");
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    }
                    break;
            }
            return this;
        }

        public Builder labels(List<Labels> labels) {
            List<String> labelsValue = labels.stream().map(Labels::getValue).collect(toList());
            issueFilter.setLabels(labelsValue);
            mergeRequestFilter.setLabels(labelsValue);
            return this;
        }

        public Builder state(Constants.MergeRequestState mergeRequestState) {
            issueFilter = null;
            mergeRequestFilter.setState(mergeRequestState);
            return this;
        }

        public Builder state(Constants.IssueState issueState) {
            issueFilter.setState(issueState);
            mergeRequestFilter = null;
            return this;
        }

        public Builder project(int projectId) { //если -1, то все из группы gl.projectsGroupId
            this.projectId = projectId;
            return this;
        }

        public ProjectsDataReader build() {
            return new ProjectsDataReader(issueFilter, mergeRequestFilter, projectId);
        }
    }
}

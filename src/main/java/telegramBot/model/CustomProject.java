package telegramBot.model;

import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;

import java.util.List;

public class CustomProject {
    private final Project project;
    private final List<Issue> issues;
    private final List<MergeRequest> mergeRequests;

    public CustomProject(Project project, List<Issue> issues, List<MergeRequest> mergeRequests) {
        this.project = project;
        this.issues = issues;
        this.mergeRequests = mergeRequests;
    }

    public Project getProject() {
        return project;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public List<MergeRequest> getMergeRequests() {
        return mergeRequests;
    }
}

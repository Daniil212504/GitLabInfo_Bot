package telegramBot.readers;

import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;
import telegramBot.handlers.ExceptionHandler;
import telegramBot.utils.Property;

import java.util.*;

import static java.lang.Boolean.parseBoolean;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;

public class ProjectsReader {
    protected static List<Project> projects = new ArrayList<>();
    protected final GitLabApi gitLabApi;
    private final GroupApi groupApi;

    protected ProjectsReader(int projectId) {
        String GL_BASE_URL = Property.get("gl.baseUrl");
        String ACCESS_TOKEN = Property.get("gl.privateToken");

        gitLabApi = new GitLabApi(GL_BASE_URL, ACCESS_TOKEN);
        gitLabApi.setIgnoreCertificateErrors(true);
        groupApi = gitLabApi.getGroupApi();

        try {
            projects = projectId != -1 ? singletonList(readMainProject(projectId)) : readProjects();
        } catch (GitLabApiException e) {
            ExceptionHandler.handleException(e);
        }

    }

    private Project readMainProject(int projectId) throws GitLabApiException {
        return gitLabApi.getProjectApi().getProject(projectId);
    }

    private List<Project> readProjects() throws GitLabApiException {
        List<Project> projects = new ArrayList<>();
        for (Integer integer : getGroupsId()) {
            List<Project> groupApiProjects = groupApi.getProjects(integer);
            projects.addAll(groupApiProjects);
        }
        return projects;
    }

    private Set<Integer> getGroupsId() {
        Set<Integer> groupsId = Arrays.stream(Property.get("gl.projectsGroupId").split(", "))
                .map(Integer::parseInt)
                .collect(toSet());

        if (parseBoolean(Property.get("gl.groupWithSubGroups"))) {
            try {
                groupsId.addAll(getSubGroupsId());
            } catch (GitLabApiException e) {
                ExceptionHandler.handleException(e);
            }
        }

        return groupsId;
    }

    private Set<Integer> getSubGroupsId() throws GitLabApiException {
        return groupApi.getSubGroups(Property.get("gl.projectsGroupId")).stream()
                .map(Group::getId)
                .collect(toSet());
    }

}

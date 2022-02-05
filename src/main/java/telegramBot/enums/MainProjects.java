package telegramBot.enums;

import telegramBot.utils.Property;

public enum MainProjects {
    PROJECT_1(Property.get("gl.project_1_Id"), Property.get("tg.project_1_Id")),
    PROJECT_2(Property.get("gl.project_2_Id"), Property.get("tg.project_2_Id")),
    PROJECT_3(Property.get("gl.project_3_Id"), Property.get("tg.project_3_Id")),
    PROJECT_4(Property.get("gl.project_4_Id"), Property.get("tg.project_4_Id")),
    PROJECT_5(Property.get("gl.project_5_Id"), Property.get("tg.project_5_Id")),
    PROJECT_6(Property.get("gl.project_6_Id"), Property.get("tg.project_6_Id"));

    private final String projectId;
    private final String chatId;

    MainProjects(String projectId, String chatId) {
        this.projectId = projectId;
        this.chatId = chatId;
    }

    public int getProjectId() {
        return Integer.parseInt(projectId);
    }

    public String getChatId() {
        return chatId;
    }

}

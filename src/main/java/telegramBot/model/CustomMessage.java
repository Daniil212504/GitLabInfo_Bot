package telegramBot.model;

import java.util.*;

public class CustomMessage {
    private final int projectId;
    private final Map<String, Integer> chatAndMessage_id = new HashMap<>();
    private String text;
    private final Set<String> likesFromUsers = new LinkedHashSet<>();
    private final Set<String> commentedFromUsers = new LinkedHashSet<>();
    private Date sentDate;

    public CustomMessage(int projectId, String text) {
        this.projectId = projectId;
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLikeFromUser(String user) {
        likesFromUsers.add(user);
    }

    public void setCommentedFromUsers(String user) {
        commentedFromUsers.add(user);
    }

    public Set<String> getLikesFromUsers() {
        return likesFromUsers;
    }

    public Set<String> getCommentedFromUsers() {
        return commentedFromUsers;
    }

    public void setChatId_and_MessagesId(String chatId, Integer messageId) {
        chatAndMessage_id.put(chatId, messageId);
    }

    public int getProjectId() {
        return projectId;
    }

    public String getText() {
        return text;
    }

    public void setSentDate(Date date) {
        sentDate = date;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public Map<String, Integer> getChatId_and_MessagesId() {
        return chatAndMessage_id;
    }

}

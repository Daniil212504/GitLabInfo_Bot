package telegramBot.handlers;

import telegramBot.enums.MainProjects;
import telegramBot.utils.Property;

import java.util.Arrays;

public abstract class Handler {
    public static final String mainChatId = Property.get("tg.mainChatId");
    public static final String helpChatId = Property.get("tg.helpChat");
    protected static final String stickerUpload = Property.get("tg.StickerUpload");

    protected static int getProjectId(String chatId) {
        return Arrays.stream(MainProjects.values())
                .filter(mP -> mP.getChatId().equals(chatId))
                .map(MainProjects::getProjectId)
                .findFirst().orElse(-1);
    }
}

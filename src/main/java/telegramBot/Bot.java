package telegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.handlers.ExceptionHandler;
import telegramBot.utils.Property;

import static telegramBot.handlers.CallbackQueryHandler.handleCallBackQuery;
import static telegramBot.handlers.CommandHandler.handleCommand;

public class Bot extends TelegramLongPollingBot {
    private final String username = Property.get("bot.username");
    private final String token = Property.get("bot.token");

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        new Thread(() -> {
            try {
                if (update.hasCallbackQuery()) {
                    handleCallBackQuery(update.getCallbackQuery());
                } else if (update.hasMessage()) {
                    Message message = update.getMessage();
                    if (message.isCommand())
                        handleCommand(message);
                }
            } catch (Throwable e) {
                ExceptionHandler.handleException(e);
            }
        }).start();
    }
}

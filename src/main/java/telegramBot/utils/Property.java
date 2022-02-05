package telegramBot.utils;

import telegramBot.handlers.ExceptionHandler;

import java.io.*;
import java.util.Properties;

public class Property {
    private static final Properties properties = new Properties();

    static {
        String[] resources = {"gitlab.properties", "bot.properties", "telegram.properties", "notification.properties"};

        InputStream inputStream = null;
        InputStreamReader reader;
        for (String resource : resources) {
            try {
                inputStream = Property.class.getClassLoader().getResourceAsStream(resource);
                reader = new InputStreamReader(inputStream, "Windows-1251");
                properties.load(reader);
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        ExceptionHandler.handleException(e);
                    }
                }
            }
        }
    }

    public static String get(String propertyName) {
        return properties.getOrDefault(propertyName, "Не удалось найти значение свойства \"" + propertyName + "\"").toString();
    }

}


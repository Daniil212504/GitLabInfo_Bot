package telegramBot.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.joining;

public class Helper {

    public static String toString(Collection<?> l) {
        if (isNull(l))
            return "";
        List<?> modified = new ArrayList<>(l);
        return IntStream.range(0, l.size())
                .mapToObj(i -> format("\n<b>%4d)</b>\t%s", i + 1, modified.get(i)))
                .collect(joining());
    }

    public static Date dateAfter(String afterDateInMinutes) {
        return new Date(currentTimeMillis() - MINUTES.toMillis(parseLong(afterDateInMinutes)));
    }

    public static String regexFrom(String plainString) { //возвращает строку с экранированными спецсимволами
        StringBuilder sb = new StringBuilder();
        char[] stringChars = plainString.toCharArray();
        for (char c : stringChars) {
            if (isSpecial(c)) sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean isSpecial(char c) {
        return ".+*?|\\()[]{}^$".indexOf(c) >= 0;
    }

    public static String bold(String text) {
        return "<b>" + text + "</b>";
    }

}

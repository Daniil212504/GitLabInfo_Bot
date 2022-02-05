package telegramBot.messageBuilders;

import telegramBot.enums.Labels;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static telegramBot.utils.Helper.bold;

public abstract class Messages {

    protected static String labelsToString(List<String> labels) {
        return labels.isEmpty()
                ? ""
                : "\nLabels: " + Labels.getNames(labels).stream()
                .map(l -> "#" + l.toLowerCase())
                .collect(joining(", "));
    }

    protected static String titleWithUrl(String url, String title) {
        return "Title: <a href=\"" + url + "\">" + title + "</a>";
    }

    public static String messageHeader(String title, Labels labels) {
        return bold(title) + " (#" + labels.name().toLowerCase() + ")\n\n";
    }


}

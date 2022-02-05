package telegramBot.enums;

import telegramBot.handlers.ExceptionHandler;
import telegramBot.utils.Helper;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public enum Labels {
    PRIORITY_HIGH("priority::high")
    , PRIORITY_MEDIUM("priority::medium")
    , PRIORITY_MINOR("priority::minor")
    , STATE_DEFECT("state::defect")
    , STATE_DOING("state::doing")
    , STATE_EXCLUDED("state::excluded")
    , STATE_REVIEW("state::review")
    , STATE_WAITING_FOR_ANSWER("state::waiting for answer")
    , TASK_TYPE_CASE("task_type::case")
    , TASK_TYPE_EVOLUTION("task_type::evolution")
    , TASK_TYPE_QUESTION("task_type::question")
    , TASK_TYPE_SUGGESTION("task_type::suggestion");

    private final String value;

    Labels(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> getNames(List<String> values) {
        Labels[] labels = Labels.values();
        List<String> result = Arrays.stream(labels)
                .filter(l -> values.stream().anyMatch(v -> v.equals(l.getValue())))
                .map(Enum::name)
                .collect(toList());

        if (result.size() != values.size()) {
            String notFoundLabels = Helper.toString(values.stream()
                    .filter(v -> Arrays.stream(labels).noneMatch(l -> v.equals(l.getValue())))
                    .collect(toList()));
            try {
                throw new Exception("В enum Labels отсутствуют: " + notFoundLabels);
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
        }

        return result;
    }

}

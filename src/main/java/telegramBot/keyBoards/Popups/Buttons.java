package telegramBot.keyBoards.Popups;

public enum Buttons {
    YES("Да", "Да"), NO("Нет", "Нет"),
    APPROVED("Approve", "Approved: "), COMMENTED("Comment", "Commented: ");

    private final String buttonText;
    private final String text;

    Buttons(String buttonText, String text) {
        this.buttonText = buttonText;
        this.text = text;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getText() {
        return text;
    }
}

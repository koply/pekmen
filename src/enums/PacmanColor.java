package enums;

public enum PacmanColor {
    BLUE("blue"), ORANGE("orange"), WHITE("white");

    public final String text;
    PacmanColor(String text) {
        this.text = text;
    }
}
package enums;

public enum GhostColor {
    CYAN("cyan"), GREEN("green"), PURPLE("purple");

    public final String text;
    GhostColor(String text) {
        this.text = text;
    }
}
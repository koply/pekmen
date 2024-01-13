package enums;

public enum Direction {
    DOWN("down"), LEFT("left"), RIGHT("right"), UP("up");

    Direction(String text) {
        this.text = text;
    }
    public final String text;
}
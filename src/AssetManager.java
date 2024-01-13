import enums.Direction;
import enums.GhostColor;
import enums.PacmanColor;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private final MainGameEngine gameEngine;
    public AssetManager(MainGameEngine engine) {
        gameEngine = engine;
        loadImages();
    }

    // PUBLIC API ----------
    // returns the direction's first index. the array that inside of the assetColorMap
    public int getFirstIndexForDirection(Direction dir) {
        return dir.ordinal()*3;
    }

    // returns images with current mode's pacman color
    public Image[] getPacmanAssetWithCurrentColor() {
        return assetColorMap.get(getCurrentPacmanColor());
    }

    // returns default pacman image with current color theme
    public Image getPacmanWithCurrentColor() {
        return pacmanColorMap.get(getCurrentPacmanColor());
    }

    // returns current pacman color (beast mode vs normal mode)
    public PacmanColor getCurrentPacmanColor() {
        return gameEngine.isBeastMode() ? PacmanColor.WHITE : PacmanColor.ORANGE;
    }

    public GhostColor getCurrentGhostColor() {
        return gameEngine.isBeastMode() ? GhostColor.CYAN : GhostColor.PURPLE;
    }

    // returns ghost image with current ghost color
    public Image getGhostWithCurrentColor() {
        return ghostColorMap.get(getCurrentGhostColor());
    }

    public Map<PacmanColor, Image> getPacmanColorMap() {
        return pacmanColorMap;
    }

    public Image getHeart() {
        return heart;
    }

    public Image getWelcome() {
        return welcome;
    }

    public Image getGameOver() {
        return gameOver;
    }

    public Image getWin() {
        return win;
    }

    // PRIVATE INTERNAL API --------------
    private Image heart, welcome, gameOver, win;

    // color -> color's array
    private final Map<PacmanColor, Image[]> assetColorMap = new HashMap<>();
    private final Map<PacmanColor, Image> pacmanColorMap = new HashMap<>();
    private final Map<GhostColor, Image> ghostColorMap = new HashMap<>();

    private void loadImages() {
        String prePath = "resources/images/";

        for (PacmanColor pacmanColor : PacmanColor.values()) {
            String color = pacmanColor.text;
            Image[] colorArray = new Image[12];
            int i = 0;
            for (Direction dir : Direction.values()) {
                for (int j = 1; j<MainGameEngine.PACMAN_ANIM_COUNT; j++) {
                    String path = prePath + color + "/" + color + "-" + dir.text + "-" + j + ".png";
                    colorArray[i++] = new ImageIcon(path).getImage();
                }
            }
            assetColorMap.put(pacmanColor, colorArray);

            String pacmanColorPath = prePath + color + "/pacman-" + color + ".png";
            pacmanColorMap.put(pacmanColor, new ImageIcon(pacmanColorPath).getImage());
        }

        for (GhostColor ghostColor : GhostColor.values()) {
            String color = ghostColor.text;
            String path = prePath + "ghost_" + color + ".png";
            ghostColorMap.put(ghostColor, new ImageIcon(path).getImage());
        }

        heart = new ImageIcon("resources/images/new_heart.png").getImage();
        welcome  = new ImageIcon("resources/images/welcome.png").getImage();
        gameOver  = new ImageIcon("resources/images/game_over.png").getImage();
        win = new ImageIcon("resources/images/youwin.png").getImage();
    }

}
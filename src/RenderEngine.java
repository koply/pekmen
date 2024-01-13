import enums.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

public class RenderEngine {

    private final MainGameEngine gameEngine;
    private final AssetManager assetManager;

    public RenderEngine(MainGameEngine engine) {
        this.gameEngine = engine;
        this.assetManager = new AssetManager(gameEngine);
    }

    public static final BasicStroke stroke = new BasicStroke(2);

    public static final int dotSize_x = 4;
    public static final int dotSize_y = 4;

    // osd -> on screen display
    public static final Color OSDCOLOR = new Color(96, 128, 255);
    public static final Font OSDFONT = MainGameEngine.smallFont;

    private int pacAnimCount = MainGameEngine.PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;

    //
    private int fps = 0;
    private long lastns = System.currentTimeMillis();
    private int frameCount = 0;
    public void showFps(Graphics2D g2d, JComponent jc) {
        frameCount++;
        long ns = System.currentTimeMillis();
        if (ns - lastns >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastns = ns;
        }
        String txt = "FPS: " + fps;
        FontMetrics metr = jc.getFontMetrics(OSDFONT);

        g2d.setColor(OSDCOLOR);
        g2d.setFont(OSDFONT);
        g2d.drawString(txt, ((MainGameEngine.SCREEN_SIZE - metr.stringWidth(txt)) / 2) - 170, MainGameEngine.SCREEN_SIZE + 16);
    }

    public void drawBeastCountdown(Graphics2D g2d, JComponent jc, int seconds) {
        String txt = "Beast Seconds: " + seconds;
        FontMetrics metr = jc.getFontMetrics(OSDFONT);
        g2d.setColor(OSDCOLOR);
        g2d.setFont(OSDFONT);
        g2d.drawString(txt, (MainGameEngine.SCREEN_SIZE - metr.stringWidth(txt)) / 2, MainGameEngine.SCREEN_SIZE + 16);
    }

    public void drawGhost(Graphics2D g2d, ImageObserver observer, int x, int y) {
        g2d.drawImage(assetManager.getGhostWithCurrentColor(), x, y, observer);
    }

    public void showIntroScreen(Graphics2D g2d, JComponent jc) {

        g2d.setColor(new Color(0, 32, 48, 204));
        g2d.fillRect(0, 0, jc.getWidth(), jc.getHeight());
        g2d.setColor(Color.white);
        g2d.drawRect(50, MainGameEngine.SCREEN_SIZE / 2 - 30, MainGameEngine.SCREEN_SIZE - 100, 50);

        // Pacman resmini çiz
        int imageWidth = jc.getWidth() -100;
        int imageHeight = imageWidth * assetManager.getWelcome().getHeight(null) / assetManager.getWelcome().getWidth(null);
        int imageX = (jc.getWidth() - imageWidth) / 2;
        int imageY = 50;
        g2d.drawImage(assetManager.getWelcome(), imageX, imageY, imageWidth, imageHeight, null);

        String s = "Press g key to start.";
        FontMetrics metr = jc.getFontMetrics(OSDFONT);

        g2d.setColor(Color.white);
        g2d.setFont(OSDFONT);
        g2d.drawString(s, (MainGameEngine.SCREEN_SIZE - metr.stringWidth(s)) / 2, MainGameEngine.SCREEN_SIZE / 2);

        // Başlık çiz
        String titleText = "Authors";
        g2d.setColor(Color.red);
        g2d.setFont(new Font("Arial", Font.PLAIN, 25));
        int titleTextX = (jc.getWidth() - g2d.getFontMetrics().stringWidth(titleText)) / 2;
        int titleTextY =  imageHeight +400;
        g2d.drawString(titleText, titleTextX, titleTextY);

        // İçeriğe yazanları çiz
        String[] authors = {"koply", "andremon"};
        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));

        int authorY = titleTextY +30;
        for (String author : authors) {
            int authorX = (jc.getWidth() - g2d.getFontMetrics().stringWidth(author)) / 2;
            g2d.drawString(author, authorX, authorY);
            authorY += 20;
        }
    }

    public void showDeathScreen(Graphics2D g2d, JComponent jc,int scorePoint,int currentLevel) {

        g2d.setColor(new Color(0, 0, 0, 255));
        g2d.fillRect(0, 0, jc.getWidth(), jc.getHeight());
        g2d.setColor(Color.white);

        int imageWidth = jc.getWidth() -100;
        int imageHeight = imageWidth * assetManager.getGameOver().getHeight(null) / assetManager.getGameOver().getWidth(null);
        int imageX = (jc.getWidth() - imageWidth) / 2;
        int imageY = 100;
        g2d.drawImage(assetManager.getGameOver(), imageX, imageY, imageWidth, imageHeight, null);

        FontMetrics metr = jc.getFontMetrics(OSDFONT);
        g2d.setColor(Color.white);
        g2d.setFont(OSDFONT);
        String score = "Your Score: "+ scorePoint;
        g2d.drawString(score, (MainGameEngine.SCREEN_SIZE - metr.stringWidth(score)) / 2, MainGameEngine.SCREEN_SIZE / 2+100);

        String level = "Level: "+ currentLevel;
        g2d.drawString(level, (MainGameEngine.SCREEN_SIZE / 2 - 30) , MainGameEngine.SCREEN_SIZE / 2 + 150);

        String s = "Press g key to play again.";
        g2d.drawString(s, (MainGameEngine.SCREEN_SIZE - metr.stringWidth(s)) / 2, MainGameEngine.SCREEN_SIZE / 2+200);

    }
    public void showLevelTransitionScreen(Graphics2D g2d, JComponent jc,int currentLevel) {

        g2d.setColor(Color.yellow);
        g2d.fillRect(0, 0, jc.getWidth(), jc.getHeight());
        g2d.setColor(Color.white);

        int imageWidth = jc.getWidth() -100;
        int imageHeight = imageWidth * assetManager.getWin().getHeight(null) / assetManager.getWin().getWidth(null);
        int imageX = (jc.getWidth() - imageWidth) / 2;
        int imageY = 50;
        g2d.drawImage(assetManager.getWin(), imageX, imageY, imageWidth, imageHeight, null);

        FontMetrics metr = jc.getFontMetrics(OSDFONT);
        g2d.setColor(Color.blue);
        g2d.setFont(new Font("Helvetica", Font.BOLD, 18));

        String level = "Current Level: "+ currentLevel;
        g2d.drawString(level, (MainGameEngine.SCREEN_SIZE - metr.stringWidth(level)) / 2, MainGameEngine.SCREEN_SIZE / 2+200);

        g2d.setFont(new Font("Helvetica", Font.BOLD, 36));

        String s = "Press enter to continue";
        g2d.drawString(s, (MainGameEngine.SCREEN_SIZE - metr.stringWidth(s))/4, MainGameEngine.SCREEN_SIZE / 2+300);

    }

    public void drawMaze(Graphics2D g2d, short[] screenData) {
        final int dotMargin_x = (MainGameEngine.BLOCK_SIZE - dotSize_x)/2;
        final int dotMargin_y = (MainGameEngine.BLOCK_SIZE - dotSize_y)/2;

        short i = 0;
        int x, y;
        int diff = MainGameEngine.BLOCK_SIZE -1;

        for (y = 0; y < MainGameEngine.SCREEN_SIZE; y += MainGameEngine.BLOCK_SIZE) {
            for (x = 0; x < MainGameEngine.SCREEN_SIZE; x += MainGameEngine.BLOCK_SIZE, i++) {

                if (screenData[i] == 0) continue;

                g2d.setColor(getBorderColor());
                g2d.setStroke(stroke);

                if ((screenData[i] & 1) != 0) { // block'un soluna duvar
                    g2d.drawLine(x, y, x, y + diff);
                }

                if ((screenData[i] & 2) != 0) { // block'un üstüne duvar
                    g2d.drawLine(x, y, x + diff, y);
                }

                if ((screenData[i] & 4) != 0) { // block'un sağına duvar
                    g2d.drawLine(x + diff, y, x + diff, y + diff);
                }

                if ((screenData[i] & 8) != 0) { // block'un altına duvar
                    g2d.drawLine(x, y + diff, x + diff, y + diff);
                }

                if ((screenData[i] & 16) != 0) { // puan yemi
                    g2d.setColor(MainGameEngine.dotColor);
                    g2d.fillRect(x + dotMargin_x, y + dotMargin_y, dotSize_x, dotSize_y);
                }

            }
        }
    }

    public void drawScore(Graphics2D g, ImageObserver observer, int score, int pacsLeft) {
        g.setFont(MainGameEngine.smallFont);
        g.setColor(OSDCOLOR);

        String s = "Score: " + score;
        g.drawString(s, MainGameEngine.SCREEN_SIZE / 2 + 176, MainGameEngine.SCREEN_SIZE + 16);

        for (int i = 0; i < pacsLeft; i++) {
            g.drawImage(assetManager.getHeart(), i * 32 + 10, MainGameEngine.SCREEN_SIZE + 1, observer);
        }
    }

    public void drawLevel(Graphics2D g2d, int level) {
        g2d.setFont(MainGameEngine.smallFont);
        g2d.setColor(OSDCOLOR);
        String s = "Level: " + level;

        g2d.drawString(s, MainGameEngine.SCREEN_SIZE / 2 + 176, MainGameEngine.SCREEN_SIZE + 30);


    }




    // view parametreleri pacmanin baktığı yön anlamına geliyor.
    // view_directionx ve view_directiony gibi yani
    public void drawPacman(Graphics2D g2d, ImageObserver observer, int pacman_x, int pacman_y, int view_dx, int view_dy) {
        Direction direction = view_dx == -1 ? Direction.LEFT :
                                 view_dx ==  1 ? Direction.RIGHT:
                                 view_dy == -1 ? Direction.UP : Direction.DOWN;

        Image[] images = assetManager.getPacmanAssetWithCurrentColor();
        int firstIndex = assetManager.getFirstIndexForDirection(direction);
        Image directionImage = pacmanAnimPos == 0
                ? assetManager.getPacmanColorMap().get(assetManager.getCurrentPacmanColor())
                : images[firstIndex + pacmanAnimPos - 1];

        g2d.drawImage(directionImage, pacman_x+1, pacman_y+1, observer);
    }

    public void drawHeart(Graphics2D g2d, ImageObserver observer, int heart_x, int heart_y) {
        if (heart_x != -1 && heart_y != -1) {
            g2d.drawImage(assetManager.getHeart(), heart_x + 1, heart_y + 1, observer); // Draw the heart.
        }
    }

    public void drawImmortal(Graphics2D g2d, ImageObserver observer) {
        g2d.drawImage(assetManager.getHeart(), MainGameEngine.SCREEN_SIZE-42, MainGameEngine.SCREEN_SIZE + 1, observer);
    }

    public void doAnim() {
        pacAnimCount--;

        if (pacAnimCount <= 0) {
            pacAnimCount = MainGameEngine.PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (MainGameEngine.PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    private final Color defaultMazeColor = new Color(44, 148, 44);
    private final BeastModeColorGenerator bmColorGen = new BeastModeColorGenerator();

    // beast mode aktifken beast mode'a göre renk dönderecek
    public Color getBorderColor() {
        boolean beast = gameEngine.isBeastMode();
        if (beast) {
            bmColorGen.doCycle();
            return bmColorGen.getColor();
        }
        return defaultMazeColor;
    }

    //OLD TEST FUNC
    /*public  void playBackgroundMusic() {
        try {
            File audioFile = new File("src/resources/");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | LineUnavailableException  | IOException e) {
            e.printStackTrace();
        }
    }*/



}
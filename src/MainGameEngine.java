import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.SecureRandom;
import java.util.Random;

import javax.swing.*;

public class MainGameEngine extends JPanel implements ActionListener {

    private boolean inGame = false;
    private boolean dying = false;

    // constant values must be public and static
    public static final Font smallFont = new Font("Helvetica", Font.BOLD, 18);
    public static final Color dotColor = new Color(192, 192, 0);
    public static final int BLOCK_SIZE = 32;
    public static final int BLOCK_SIZE_HALF = BLOCK_SIZE / 2;
    public static final int N_BLOCKS = 20;
    public static final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    public static final int PAC_ANIM_DELAY = 2;

    // default + 3 animated circles, total 4
    // animated png's name specifiers goes like blue1, blue2, blue3
    public static final int PACMAN_ANIM_COUNT = 4;
    public static final int MAX_GHOSTS = 12;
    public static final int PACMAN_SPEED = 8;

    private int N_GHOSTS = 6;
    private int pacsLeft, allGameScore, singleChapterScore;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;
    private boolean[] ghostDisappear = new boolean[MAX_GHOSTS];

    private int heart_x, heart_y;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy, view_dx, view_dy;

    private final Random secureRandom = new SecureRandom();

    private final int[] validSpeeds = {1, 2, 4, 8};
    private final int maxSpeed = 8;

    private int currentSpeed = 2;

    private short[] screenData;
    private Timer timer;

    private boolean immortalMode = false;
    private static final char immortalHackKey = 'm';
    private static final char endLevelHackKey = 'n';

    private final RenderEngine renderEngine = new RenderEngine(this);
    private final BeastModeManager beastModeManager = new BeastModeManager();

    private  int currentLevel = 1;

    private MusicPlayer mscPlyer = new MusicPlayer();

    private boolean showLevelTransitionScreen = false;

    public boolean isBeastMode() { return beastModeManager.isBeastMode(); }

    public MainGameEngine() {
        initVariables();
        initBoard();
        // mscPlyer.play("pacman_background.wav"); TODO EXCEPTION
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);
    }

    private void initVariables() {
        screenData = new short[N_BLOCKS * N_BLOCKS];
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];


        timer = new Timer(40, this);
        timer.start();
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        renderGame(g);
    }

    private void renderGame(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (showLevelTransitionScreen) {
            renderEngine.showLevelTransitionScreen(g2d, this, currentLevel);
        }
        else {
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, Pacman.WIDTH, Pacman.HEIGHT);

            if (immortalMode && !isBeastMode()) renderEngine.drawImmortal(g2d, this);
            renderEngine.drawMaze(g2d, screenData);
            renderEngine.drawHeart(g2d, this, heart_x, heart_y);
            renderEngine.drawScore(g2d, this, allGameScore, pacsLeft);
            renderEngine.drawLevel(g2d, currentLevel);
            renderEngine.showFps(g2d, this);
            renderEngine.drawBeastCountdown(g2d, this, beastModeManager.getBeastSeconds());
            renderEngine.doAnim();

            if (pacsLeft > 0) {
                if (inGame) {
                    playGame(g2d);
                } else {
                    renderEngine.showIntroScreen(g2d, this);
                }
            } else {
                inGame = false;
                renderEngine.showDeathScreen(g2d, this, allGameScore, currentLevel);
            }

            Toolkit.getDefaultToolkit().sync();
            g2d.dispose();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }

    private void playGame(Graphics2D g2d) {
        if (dying) {
            death();
        } else {
            movePacman();
            renderEngine.drawPacman(g2d, this, pacman_x, pacman_y, view_dx, view_dy);
            moveGhosts(g2d);

            // maze check
            boolean pointsEnd = GameMaps.isPointsFinished(singleChapterScore);
            if (pointsEnd && beastModeManager.getBeastSeconds() > 0 && !beastModeManager.isBeastMode()) {
                beastModeManager.beastModeOn();
                immortalMode = true;
            }
            if (pointsEnd && beastModeManager.getBeastSeconds() <= 0) {
                beastModeManager.beastModeOff();
                immortalMode = false;
                allGameScore += 50;
                singleChapterScore = 0;
                N_GHOSTS = N_GHOSTS < MAX_GHOSTS ? N_GHOSTS+1 : N_GHOSTS;
                currentSpeed = currentSpeed < maxSpeed ? currentSpeed+1 : currentSpeed;
                ghostDisappear = new boolean[12];
                currentLevel++;
                showLevelTransitionScreen = true; // Level geçiş ekranını göster
                initLevel();
            }

        }
    }
    private void death( ) {
        pacsLeft--;

        if (pacsLeft == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {
        int pos;
        int count;

        for (short i = 0; i < N_GHOSTS; i++) {
            if (ghostDisappear[i]) continue;

            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                } if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                } if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                } if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            renderEngine.drawGhost(g2d, this, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - BLOCK_SIZE_HALF) && pacman_x < (ghost_x[i] + BLOCK_SIZE_HALF)
                    && pacman_y > (ghost_y[i] - BLOCK_SIZE_HALF) && pacman_y < (ghost_y[i] + BLOCK_SIZE_HALF)
                    && inGame) {

                if (!immortalMode) {
                    dying = true;
                } else if (beastModeManager.isBeastMode()) {
                    ghostDisappear[i] = true;
                    allGameScore += 100;
                }

            }
        }
    }

    private void movePacman() {
        int pos;
        short blockData;

        // requested_direction_x
        // requested_direction_y
        // pacman_direction_x
        // pacman_direction_y
        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        }

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (pacman_y / BLOCK_SIZE);
            blockData = screenData[pos];

            // gecerli blokta yem kontrolu, varsa score++ ve bloktaki yemi temizleme
            if ((blockData & 16) != 0) {
                screenData[pos] = (short) (blockData & 15);
                allGameScore++;
                singleChapterScore++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (blockData & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (blockData & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (blockData & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (blockData & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (blockData & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (blockData & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (blockData & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (blockData & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }

        }
        if (pacsLeft!=3){
            if (pacman_x == heart_x && pacman_y == heart_y) {
                pacsLeft++;
                heart_x = -1;
                heart_y = -1;
            }
        }

        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void initGame() {
        pacsLeft = 3;
        allGameScore = 0;
        singleChapterScore = 0;
        initLevel();
        N_GHOSTS = 6;
        currentSpeed = 3;
        if (pacsLeft >= 3) {
            heart_x = -1;
            heart_y = -1;
        } else {
            initHeart();
        }
    }

    private final short[] levelData = GameMaps.newMap;

    private void initLevel() {
        // array copy optimization
        System.arraycopy(levelData, 0, screenData, 0, N_BLOCKS * N_BLOCKS);

        continueLevel();
    }

    private void initHeart() {
        do {
            heart_x = (int) (Math.random() * N_BLOCKS) * BLOCK_SIZE;
            heart_y = (int) (Math.random() * N_BLOCKS) * BLOCK_SIZE;
        } while ((screenData[heart_x / BLOCK_SIZE + N_BLOCKS * (heart_y / BLOCK_SIZE)] & 16) == 0 || !isAccessible(heart_x, heart_y));
    }

    private boolean isAccessible(int x, int y) {
        // Check if the given position is within the maze.
        if (x < 0 || x >= N_BLOCKS * BLOCK_SIZE || y < 0 || y >= N_BLOCKS * BLOCK_SIZE) {
            return false;
        }

        // Check if the corresponding element in the screenData array represents a wall.
        return (screenData[x / BLOCK_SIZE + N_BLOCKS * (y / BLOCK_SIZE)] & 15) != 15;
    }

    private void continueLevel() {
        for (int i = 0; i < N_GHOSTS; i++) {
            ghost_y[i] = 4 * BLOCK_SIZE;
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = i % 2 == 0 ? 1 : -1; // Alternate direction for initial movement.
            int random = secureRandom.nextInt(4);
            if (random > currentSpeed) {
                random = currentSpeed;
            }
            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 6 * BLOCK_SIZE;
        pacman_y = 10 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        view_dx = -1;
        view_dy = 0;
        dying = false;

        if (pacsLeft < 3) {
            initHeart();
        }

    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();
            if (inGame) {
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                }

                else if (key == KeyEvent.VK_PAUSE) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }

                else if (!beastModeManager.isBeastMode() && key == endLevelHackKey || key == Character.toUpperCase(endLevelHackKey)) {
                    for (int i = 0; i < screenData.length; i++) {
                        if ((screenData[i] & 16) != 0) {
                            allGameScore++;
                            singleChapterScore++;
                            screenData[i] -= 16;
                        }
                    }
                } else if (!beastModeManager.isBeastMode() && key == immortalHackKey || key == Character.toUpperCase(immortalHackKey)) {
                    immortalMode = !immortalMode;
                }
                else if (key == KeyEvent.VK_ENTER) {
                    if (showLevelTransitionScreen) {
                        showLevelTransitionScreen = false; // Level geçiş ekranını kapat
                        initGame(); // Yeni leveli başlat
                    }
                }
            }

            else {
                if (key == 'g' || key == 'G') {
                    inGame = true;
                    currentLevel = 1;
                    initGame();
                }


            }
        }

    }

}

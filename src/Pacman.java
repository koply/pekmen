import java.awt.EventQueue;
import javax.swing.JFrame;

public class Pacman extends JFrame {

    public Pacman() {
    }

    public static final int WIDTH = 660;
    public static final int HEIGHT = 720;

    private void initUI() {
        add(new MainGameEngine());
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            var game = new Pacman();
            game.initUI();
            game.setVisible(true);
        });
    }
}

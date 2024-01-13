import java.util.Timer;
import java.util.TimerTask;

public class BeastModeManager {

    public static final int beastModeDefaultSeconds = 10;

    private boolean beastMode = false;
    private int beastModeSeconds = beastModeDefaultSeconds;

    private Timer countdownTimer;

    public BeastModeManager() {
    }

    public boolean isBeastMode() {
        return beastMode;
    }

    public synchronized int getBeastSeconds() {
        return beastModeSeconds;
    }

    public void beastModeOn() {
        beastMode = true;
        countdownTimer = new Timer();
        countdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                beastModeSeconds -= 1;
            }
        }, 0, 1000);
    }

    public void beastModeOff() {
        countdownTimer.cancel();
        beastMode = false;
        beastModeSeconds = beastModeDefaultSeconds;
    }
}
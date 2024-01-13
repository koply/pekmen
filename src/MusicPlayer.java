import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class MusicPlayer {

    private Clip clip;
    private boolean repeat;

    public MusicPlayer() {
        try {
            this.clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        this.repeat = false;
    }

    public void play(String wav) {
        String url = "wav/" + wav;
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(url);
        try {
            AudioInputStream aio = AudioSystem.getAudioInputStream(resourceStream);
            AudioFormat audioFormat = aio.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(aio);
            audioClip.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAsync(String wavPath) {
        new Thread(() -> {
            try {
                clip = AudioSystem.getClip();
                if (clip.isRunning()) {
                    clip.stop();
                }
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResourceAsStream("wav/" + wavPath));
                //ERROR: java.lang.NullPointerException

                clip.open(audioInputStream);
                clip.start();

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        if (repeat) {
                            clip.setMicrosecondPosition(0);
                            clip.start();
                        } else {
                            clip.stop();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public  void StopMusic() {
        new Thread(() -> {
            try {

                repeat = false;
                clip.stop();

            } catch (Exception e) {
                System.out.println(e);
            }
        }).start();
    }


    public void RepeatMusic(boolean repeat){

        this.repeat = repeat;

    }

}

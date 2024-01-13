import java.awt.*;

public class BeastModeColorGenerator {

    public BeastModeColorGenerator() {
    }

    /* RGB
    * 255,   0,  0
    * 255, 255,  0
    * 0,   255,  0
    * 0,   255, 255
    * 0,     0, 255
    * 255,   0, 255
    * 255,   0,  0   */

    private byte animationSpeed = 5;
    public byte getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(byte animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    // [0-255]
    private short red = 255, green = 0, blue = 0;
    private byte dRed = 0, dGreen = 1, dBlue = 0; // direction

    private long last = System.currentTimeMillis();
    public void doCycle() {
        long ms = System.currentTimeMillis();
        if (ms - last < 1) {
            return;
        }
        last = ms;

        if (red == 255 && green == 0 && blue == 0) {
            dRed = 0;
            dGreen = animationSpeed;
            dBlue = 0;
        } else if (red == 255 && green == 255 && blue == 0) {
            dRed = (byte) -animationSpeed;
            dGreen = 0;
            dBlue = 0;
        } else if (red == 0 && green == 255 && blue == 0) {
            dRed = 0;
            dGreen = 0;
            dBlue = animationSpeed;
        } else if (red == 0 && green == 255 && blue == 255) {
            dRed = 0;
            dGreen = (byte) -animationSpeed;
            dBlue = 0;
        } else if (red == 0 && green == 0 && blue == 255) {
            dRed = animationSpeed;
            dGreen = 0;
            dBlue = 0;
        } else if (red == 255 && green == 0 && blue == 255) {
            dRed = 0;
            dGreen = 0;
            dBlue = (byte) -animationSpeed;
        }

        red += dRed;
        green += dGreen;
        blue += dBlue;

        red = red > 255 ? 255 : red < 0 ? 0 : red;
        green = green > 255 ? 255 : green < 0 ? 0 : green;
        blue = blue > 255 ? 255 : blue < 0 ? 0 : blue;

    }

    public void reset() {
        red = 255;  dRed = 0;
        green = 0;  dGreen = 1;
        blue = 0;   dBlue = 0;
    }

    public Color getColor() {
        return new Color(red, green, blue);
    }

    public short getRed() {
        return red;
    }

    public short getGreen() {
        return green;
    }

    public short getBlue() {
        return blue;
    }
}
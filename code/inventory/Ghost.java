package code.inventory;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class Ghost extends Block{
    private KeyCode direction;
    private double speed;
    private long lastChangedCourse;
    private boolean chase;
    private boolean spooky;
    private long lasSeenPacman;

    public Ghost(String type, Image image, double posX, double posY) {
        super(type, image, posX, posY);
        this.direction = null;
        this.speed = 0.5;
        lastChangedCourse = System.currentTimeMillis();
        chase = false;
        spooky = false;
    }

    public KeyCode getDirection() {
        return direction;
    }

    public double getSpeed() {
        return speed;
    }

    public long getLastChangedCourse() {
        return lastChangedCourse;
    }

    public boolean getChase() {
        return chase;
    }

    public long getLasSeenPacman() {
        return lasSeenPacman;
    }

    public void startChase(boolean chase) {
        this.chase = chase;
    }

    public void setDirection(KeyCode dir) {
        this.direction = dir;
    }

    public void setLastChangedCourse(long num) {
        this.lastChangedCourse = num;
    }

    public void setInitialImage() {
        setImage(getImage());
    }

    public boolean getSpooky() {
        return spooky;
    }

    public void setSpooky(boolean b) {
        spooky = b;
    }

    public void setLastSeenPacman(long l) {
        lasSeenPacman = l;
    }
}
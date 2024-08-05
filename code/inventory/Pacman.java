package code.inventory;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class Pacman extends Block{
    private double speed;
    private KeyCode direction;
    private boolean moving;
    final private double initialPosX;
    final private double initialPosY;

    public Pacman(String type, Image image, double posX, double posY) {
        super(type, image, posX, posY);
        this.speed = 0.5;
        this.direction = null;
        this.moving = false;
        initialPosX = posX;
        initialPosY = posY;
    }

    public double getSpeed() {
        return speed;
    }

    public KeyCode getDirection() {
        return direction;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setDirection(KeyCode direction) {
        this.direction = direction;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public double getInitialPosX() {
        return initialPosX;
    }

    public double getInitialPosY() {
        return initialPosY;
    }
}
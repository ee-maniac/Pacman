package code.inventory;

import javafx.scene.image.*;

public class Block {
    private String type;
    private ImageView imageView;
    private double posX;
    private double posY;
    private Image image;

    public Block(String type, Image image, double posX, double posY) {
        this.type = type;
        this.image = image;
        this.imageView = new ImageView(image);
        this.posX = posX;
        this.posY = posY;
        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());
        imageView.setX(posX);
        imageView.setY(posY);
    }

    public String getType() {
        return type;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setPosX(double posX) {
        this.posX = posX;
        imageView.setX(posX);
    }

    public void setPosY(double posY) {
        this.posY = posY;
        imageView.setY(posY);
    }

    public void setImage(Image image) {
        imageView.setImage(image);
    }

    public Image getImage() {
        return image;
    }
}
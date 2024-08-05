package code;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class HomeController {
    private Main mainApp;
    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    ImageView map1;
    @FXML
    ImageView map2;
    @FXML
    ImageView map3;
    @FXML
    ImageView map4;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void initialize() {
        try {
            media = new Media(new File("src/res/Pac-Man/game_start.wav").toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        }
        catch (Exception e) {
            System.out.println("File not found");
        }

        map1.setOnMouseClicked(event -> {
            mainApp.startGame("map1");
            mediaPlayer.dispose();
        });
        map2.setOnMouseClicked(event -> {
            mainApp.startGame("map2");
            mediaPlayer.dispose();
        });
        map3.setOnMouseClicked(event -> {
            mainApp.startGame("map3");
            mediaPlayer.dispose();
        });
        map4.setOnMouseClicked(event -> {
            mainApp.startGame("map4");
            mediaPlayer.dispose();
        });
    }
}
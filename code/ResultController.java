package code;

import code.inventory.Block;
import code.inventory.Ghost;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class ResultController {
    private Main mainApp;
    private String map;
    private boolean result;
    private long score;
    private long x = 0;
    AnimationTimer timer;
    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    ImageView resultView;
    @FXML
    Label scoreLabel;
    @FXML
    ImageView replay;
    @FXML
    ImageView home;
    @FXML
    Image winImage = new Image("file:src/res/win_sign.png");
    @FXML
    Image loseImage = new Image("file:src/res/lose_sign.png");

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setScore(long score) {
        this.score = score;
        run();
    }

    public void initialize() {
        replay.setOnMouseClicked(event -> {
            mainApp.startGame(map);
            mediaPlayer.dispose();
        });
        home.setOnMouseClicked(event -> {
            mainApp.startHome();
            mediaPlayer.dispose();
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(x <= score) {
                    scoreLabel.setText("SCORE: " + x);
                    x += 25;
                }
            }
        };
    }

    private void run() {
        String path;
        if(result) {
            path = "src/res/Pac-Man/intermission.wav";
            resultView.setImage(winImage);
        }
        else {
            path = "src/res/Pac-Man/death_1.wav";
            resultView.setImage(loseImage);
        }

        try {
            media = new Media(new File(path).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
        catch (Exception e) {
            System.out.println("File not found");
        }

        timer.start();
    }
}
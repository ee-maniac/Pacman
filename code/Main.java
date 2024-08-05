package code;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private boolean firstTime = true;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Pac-Man");
        startHome();
    }

    public void startHome() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Home.fxml"));
        Pane homeRoot = null;
        try {
            homeRoot = loader.load();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        HomeController homeController = loader.getController();
        homeController.setMainApp(this);

        setScene(homeRoot, "/res/styles.css");
    }

    public void startGame(String map) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Game.fxml"));
        Pane gameRoot = null;
        try {
            gameRoot = loader.load();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        GameController gameController = loader.getController();
        gameController.setMainApp(this);
        gameController.setMap(map);

        setScene(gameRoot, "/res/styles.css");
    }

    public void startResult(String map, boolean result, long score) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Result.fxml"));
        Pane resultRoot = null;
        try {
            resultRoot = loader.load();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        ResultController resultController = loader.getController();
        resultController.setMainApp(this);
        resultController.setMap(map);
        resultController.setResult(result);
        resultController.setScore(score);

        setScene(resultRoot, "/res/styles.css");
    }

    private void setScene(Pane root, String path) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(path).toExternalForm());
        primaryStage.setScene(scene);
        if(firstTime) {
            primaryStage.show();
            firstTime = false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
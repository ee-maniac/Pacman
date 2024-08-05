package code;

import code.inventory.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameController {
    private Main mainApp;
    private String map;
    private Maze maze;
    private Pacman pacman;
    private long score = 0;
    private long scoreRemaining = 0;
    private int lives = 3;
    private Image cherry = new Image("file:src/res/cherry.png");
    private boolean cherryEaten = false;
    private boolean chase = false;
    private long clockTick = 0;
    private long stopTime = 0;
    private long spookTimer = 0;
    private long cherryTime = 0;
    private AnimationTimer timer;
    private MediaPlayer backgroundPlayer = new MediaPlayer((new Media(new File("src/res/Pac-Man/siren_1.wav").toURI().toString())));
    private MediaPlayer chasePlayer = new MediaPlayer((new Media(new File("src/res/Pac-Man/siren_3.wav").toURI().toString())));
    private MediaPlayer specialPlayer = new MediaPlayer((new Media(new File("src/res/Pac-Man/eat_fruit.wav").toURI().toString())));
    private MediaPlayer ghostPlayer = new MediaPlayer((new Media(new File("src/res/Pac-Man/eat_ghost.wav").toURI().toString())));
    private MediaPlayer fruitPlayer = new MediaPlayer((new Media(new File("src/res/Pac-Man/munch_2.wav").toURI().toString())));

    @FXML
    private Pane pane1;
    @FXML
    private Pane pane2;
    @FXML
    private Label scoreLabel;
    @FXML
    private HBox gameBar;

    private KeyCode lastKeyPressed = null;
    private final Random random = new Random();

    public void setMap(String map) {
        maze = new Maze(map);
        this.map = map;
        pacman = maze.getPacman();
        run();
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    private void run() {
        backgroundPlayer.play();
        backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        chasePlayer.setCycleCount(MediaPlayer.INDEFINITE);

        for(int i = 0; i < maze.getMaze().size(); i++) {
            for (int j = 0; j < maze.getMaze().get(i).size(); j++) {
                pane1.getChildren().add(maze.getMaze().get(i).get(j).getImageView());
            }
        }

        for(int i = 0; i < maze.getFruits().size(); i++) {
            pane2.getChildren().add(maze.getFruits().get(i).getImageView());
        }

        pane2.getChildren().add(pacman.getImageView());
        for(int i = 0; i < maze.getGhosts().size(); i++) {
            pane2.getChildren().add(maze.getGhosts().get(i).getImageView());
            maze.getGhosts().get(i).setDirection(getRandomDirection(null));
        }

        pane2.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            if(key == KeyCode.UP || key == KeyCode.DOWN || key == KeyCode.LEFT || key == KeyCode.RIGHT || key == KeyCode.W || key == KeyCode.A || key == KeyCode.S || key == KeyCode.D) {
               if(key == KeyCode.W) {
                   key = KeyCode.UP;
               }
                if(key == KeyCode.S) {
                    key = KeyCode.DOWN;
                }
                if(key == KeyCode.A) {
                    key = KeyCode.LEFT;
                }
                if(key == KeyCode.D) {
                    key = KeyCode.RIGHT;
                }
                lastKeyPressed = key;
                pacman.setMoving(true);
            }
        });

        pane2.setFocusTraversable(true);
        pane2.requestFocus();

        for(int i = 0; i < maze.getFruits().size(); i++) {
            if(maze.getFruits().get(i).getType().equals("smallF")) {
                scoreRemaining += 75;
            }
            if(maze.getFruits().get(i).getType().equals("bigF")) {
                scoreRemaining += 150;
            }
        }

        fruitPlayer.setOnEndOfMedia(() -> {
            fruitPlayer.seek(fruitPlayer.getStartTime());
            fruitPlayer.play();
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(pacman.isMoving()) {
                    int x = 0;
                    for(Block b : maze.getFruits()) {
                        if(b.getType().equals("bigF") || b.getType().equals("smallF")) {
                            x++;
                        }
                    }
                    if(x == 0) {
                        timer.stop();
                        mainApp.startResult(maze.getMap(), true, score);
                    }

                    Iterator<Block> it = maze.getFruits().iterator();

                    while(it.hasNext()) {
                        Block block = it.next();
                        if(block.getType().equals("clock") && System.currentTimeMillis() - clockTick > 10000) {
                            pane2.getChildren().remove(block.getImageView());
                            it.remove();
                        }

                        if(block.getType().equals("cherry") && System.currentTimeMillis() - cherryTime > 40000) {
                            pane2.getChildren().remove(block.getImageView());
                            it.remove();
                        }

                        if(charCollision(pacman, block)) {
                            pane2.getChildren().remove(block.getImageView());
                            switch(block.getType()) {
                                case "bigF" -> {
                                    score += 150;
                                    maze.getRoundScore()[-lives + 3] += 150;
                                    terrorBegin();
                                    specialPlayer.seek(specialPlayer.getStartTime());
                                    specialPlayer.play();
                                    specialPlayer.setOnEndOfMedia(() -> specialPlayer.stop());
                                }
                                case "smallF" -> {
                                    score += 75;
                                    maze.getRoundScore()[-lives + 3] += 75;
                                    fruitPlayer.seek(fruitPlayer.getStartTime());
                                    fruitPlayer.play();
                                    fruitPlayer.setOnEndOfMedia(() -> fruitPlayer.stop());
                                }
                                case "cherry" -> {
                                    score += 450;
                                    maze.getRoundScore()[-lives + 3] += 450;
                                    collectCherry();
                                    specialPlayer.seek(specialPlayer.getStartTime());
                                    specialPlayer.play();
                                    specialPlayer.setOnEndOfMedia(() -> specialPlayer.stop());
                                }
                                case "clock" -> {
                                    stopTime = System.currentTimeMillis();
                                    specialPlayer.seek(specialPlayer.getStartTime());
                                    specialPlayer.play();
                                    specialPlayer.setOnEndOfMedia(() -> specialPlayer.stop());
                                }
                            }
                            updateScoreLabel();
                            it.remove();
                        }
                    }

                    if(movePacman(lastKeyPressed, true)) {
                        pacman.setDirection(lastKeyPressed);
                        maze.setPacmanFaceDir(lastKeyPressed);
                    }

                    movePacman(pacman.getDirection(), false);
                }

                int x = 0;
                for(Ghost g : maze.getGhosts()) {
                    if(charCollision(pacman, g)) {
                        if(!g.getSpooky()) {
                            restart();
                            lives--;
                        }
                        else {
                            ghostPlayer.seek(ghostPlayer.getStartTime());
                            ghostPlayer.play();
                            ghostPlayer.setOnEndOfMedia(() -> ghostPlayer.stop());
                            if(map.equals("map1") || map.equals("map4")) {
                                g.setPosX(25);
                                g.setPosY(25);
                            }
                            else {
                                g.setPosX(75);
                                g.setPosY(25);
                            }
                            g.setInitialImage();
                            g.setSpooky(false);
                        }
                    }

                    if(canSeePacman(g) && !g.getSpooky()) {
                        if(!g.getChase()) {
                            g.startChase(true);
                            g.setLastSeenPacman(System.currentTimeMillis());
                        }
                        g.setDirection(getDirectionTowards(g));
                    }

                    if(!canSeePacman(g) && System.currentTimeMillis() - g.getLasSeenPacman() > 10000) {
                        g.startChase(false);
                    }

                    if(!g.getChase()) {
                        x++;
                        KeyCode randDir = getRandomDirection(g.getDirection());
                        if(moveGhost(g, randDir, true) && System.currentTimeMillis() - g.getLastChangedCourse() > 2000) {
                            g.setDirection(randDir);
                            g.setLastChangedCourse(System.currentTimeMillis());
                        }
                    }
                    else if(!chase) {
                        chase = true;
                        backgroundPlayer.stop();
                        chasePlayer.play();
                    }

                    if(System.currentTimeMillis() - stopTime > 10000) {
                        moveGhost(g, g.getDirection(), false);
                    }
                }

                if(x == maze.getGhosts().size()) {
                    chase = false;
                    chasePlayer.stop();
                    backgroundPlayer.play();
                }

                if(maze.getRoundScore()[-lives+3] >= scoreRemaining/3 && maze.getFruitStatus() && !cherryEaten) {
                    maze.setFruitPos(pane2);
                    maze.setFruitStatus(false);
                    cherryTime = System.currentTimeMillis();
                }

                if(chase) {
                    if(random.nextInt(200000) < 250 && System.currentTimeMillis() - clockTick > 10000 && System.currentTimeMillis() - stopTime > 10000) {
                        maze.setClock(pane2);
                        clockTick = System.currentTimeMillis();
                    }
                }
                else if(random.nextInt(200000) < 50 && System.currentTimeMillis() - clockTick > 10000 && System.currentTimeMillis() - stopTime > 10000){
                    maze.setClock(pane2);
                    clockTick = System.currentTimeMillis();
                }

                if(System.currentTimeMillis() - spookTimer > 10000 && spookTimer != 0) {
                    spookTimer = 0;
                    maze.spookyTimes(false);
                }
            }
        };

        timer.start();
    }

    private boolean movePacman(KeyCode dir, boolean test) {
        if(map.equals("map1")) {
            if(pacman.getPosX() > 36*25) {
                pacman.setPosX(0+5);
            }
            if(pacman.getPosX() < 0) {
                pacman.setPosX(36*25-5);
            }
        }

        double newX = pacman.getPosX();
        double newY = pacman.getPosY();

        if(dir != null) {
            switch (dir) {
                case UP:
                    newY -= pacman.getSpeed();
                    break;
                case DOWN:
                    newY += pacman.getSpeed();
                    break;
                case LEFT:
                    newX -= pacman.getSpeed();
                    break;
                case RIGHT:
                    newX += pacman.getSpeed();
                    break;
            }
        }

        double oldX = pacman.getPosX();
        double oldY = pacman.getPosY();

        pacman.setPosX(newX);
        pacman.setPosY(newY);
        if(collision(pacman)) {
            pacman.setPosX(oldX);
            pacman.setPosY(oldY);
            if (!test) {
                pacman.setMoving(false);
            }
            return false;
        } else {
            if(test) {
                pacman.setPosX(oldX);
                pacman.setPosY(oldY);
            }
            return true;
        }
    }

    private boolean moveGhost(Ghost ghost, KeyCode dir, boolean test) {
        if(map.equals("map1")) {
            if(ghost.getPosX() > 36*25) {
                ghost.setPosX(0+5);
            }
            if(ghost.getPosX() < 0) {
                ghost.setPosX(36*25-5);
            }
        }

        double newX = ghost.getPosX();
        double newY = ghost.getPosY();

        if(dir != null) {
            switch(dir) {
                case UP:
                    newY -= ghost.getSpeed();
                    break;
                case DOWN:
                    newY += ghost.getSpeed();
                    break;
                case LEFT:
                    newX -= ghost.getSpeed();
                    break;
                case RIGHT:
                    newX += ghost.getSpeed();
                    break;
            }
        }

        double oldX = ghost.getPosX();
        double oldY = ghost.getPosY();

        ghost.setPosX(newX);
        ghost.setPosY(newY);
        if(collision(ghost)) {
            ghost.setPosX(oldX);
            ghost.setPosY(oldY);
            if(!test) {
                ghost.setDirection(getRandomDirection(ghost.getDirection()));
            }
            return false;
        }else {
            if(test) {
                ghost.setPosX(oldX);
                ghost.setPosY(oldY);
            }
            return true;
        }
    }

    private boolean canSeePacman(Block ghost) {
        double ghostX = ghost.getPosX();
        double ghostY = ghost.getPosY();
        double pacmanX = pacman.getPosX();
        double pacmanY = pacman.getPosY();

        if(pacmanX < ghostX+25 && pacmanX+25 > ghostX) {
            double minY = Math.min(ghostY, pacmanY);
            double maxY = Math.max(ghostY, pacmanY);
            for(double y = minY; y <= maxY; y += 5) {
                if(isWallAtPosition(ghostX, y)) {
                    return false;
                }
            }
            return true;
        }else if(pacmanY < ghostY+25 && pacmanY+25 > ghostY) {
            double minX = Math.min(ghostX, pacmanX);
            double maxX = Math.max(ghostX, pacmanX);
            for(double x = minX; x <= maxX; x += 5) {
                if(isWallAtPosition(x, ghostY)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isWallAtPosition(double x, double y) {
        ArrayList<List<Block>> map = maze.getMaze();
        for(List<Block> blocks : map) {
            for(Block block : blocks) {
                if(block.getType().equals("wall")) {
                    double blockY = block.getPosY();
                    double blockX = block.getPosX();
                    if(x < blockX + 25 && x + 25 > blockX &&
                            y < blockY + 25 && y + 25 > blockY) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private KeyCode getDirectionTowards(Block ghost) {
        double ghostX = ghost.getPosX();
        double ghostY = ghost.getPosY();
        double pacmanX = pacman.getPosX();
        double pacmanY = pacman.getPosY();

        if(Math.abs(ghostX - pacmanX) > Math.abs(ghostY - pacmanY)) {
            if(ghostX < pacmanX) {
                return KeyCode.RIGHT;
            }
            else {
                return KeyCode.LEFT;
            }
        } else {
            if(ghostY < pacmanY) {
                return KeyCode.DOWN;
            }
            else {
                return KeyCode.UP;
            }
        }
    }

    private KeyCode getRandomDirection(KeyCode dir) {
        KeyCode[] directions = {KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT};
        KeyCode newDir;
        do {
            newDir = directions[random.nextInt(directions.length)];
        } while(oppositeDirection(dir, newDir) || newDir == dir);
        return newDir;
    }

    private boolean oppositeDirection(KeyCode oldDir, KeyCode newDir) {
        if((oldDir == KeyCode.UP && newDir == KeyCode.DOWN) || (newDir == KeyCode.UP && oldDir == KeyCode.DOWN)) {
            return true;
        }
        if((oldDir == KeyCode.RIGHT && newDir == KeyCode.LEFT) || (newDir == KeyCode.RIGHT && oldDir == KeyCode.LEFT)) {
            return true;
        }
        return false;
    }

    private boolean collision(Block character) {
        ArrayList<List<Block>> map = maze.getMaze();

        double charX = character.getPosX();
        double charY = character.getPosY();

        for(List<Block> blocks : map) {
            for(Block block : blocks) {
                if(block.getType().equals("wall")) {
                    double blockX = block.getPosX();
                    double blockY = block.getPosY();
                    double blockWidth = block.getImageView().getFitWidth();
                    double blockHeight = block.getImageView().getFitHeight();

                    if(charX < blockX + blockWidth && charX + 25 > blockX &&
                            charY < blockY + blockHeight && charY + 25 > blockY) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean charCollision(Block char1,  Block char2) {
        double char1X = char1.getPosX();
        double char1Y = char1.getPosY();
        double char2X = char2.getPosX();
        double char2Y = char2.getPosY();

        if(char2.getType().equals("bigF") || char2.getType().equals("smallF")) {
            if(char1X < char2X + 12.5 && char1X + 12.5 > char2X &&
                    char1Y < char2Y + 12.5 && char1Y + 12.5 > char2Y) {
                return true;
            }
            return false;
        }

        return char1X < char2X + 25 && char1X + 25 > char2X &&
                char1Y < char2Y + 25 && char1Y + 25 > char2Y;
    }

    private void updateScoreLabel() {
        scoreLabel.setText("SCORE: " + score);
    }

    private void restart() {
        if(lives == 1) {
            lives++;
            timer.stop();
            backgroundPlayer.dispose();
            chasePlayer.dispose();
            mainApp.startResult(maze.getMap(), false, score);
        }

        chase = false;
        clockTick = 0;
        stopTime = 0;
        cherryEaten = false;
        spookTimer = 0;
        cherryTime = 0;

        maze.spookyTimes(false);
        maze.setGhostPos();
        for(Ghost g : maze.getGhosts()) {
            g.setDirection(null);
            g.startChase(false);
            g.setLastChangedCourse(0);
        }
        pacman.setPosX(pacman.getInitialPosX());
        pacman.setPosY(pacman.getInitialPosY());
        maze.setPacmanFaceDir(KeyCode.RIGHT);
        pacman.setMoving(false);
        pacman.setDirection(null);

        for(int i = gameBar.getChildren().size() - 1; i >= 0; i--) {
            String x = gameBar.getChildren().get(i).getId();
            if( x!=null && (x.equals("life1") || x.equals("life2") || x.equals("life3"))) {
                gameBar.getChildren().remove(i);
                break;
            }
        }

        maze.removeCherry(pane2);
        maze.setFruitStatus(true);

        long x = 0;
        for(int i = 0; i < maze.getFruits().size(); i++) {
            if(maze.getFruits().get(i).getType().equals("smallF")) {
                x += 75;
            }
            if(maze.getFruits().get(i).getType().equals("bigF")) {
                x += 150;
            }
        }
        scoreRemaining = x;
    }

    private void collectCherry() {
        ImageView imageView = new ImageView(cherry);
        imageView.setFitWidth(cherry.getWidth());
        imageView.setFitHeight(cherry.getHeight());
        gameBar.getChildren().add(imageView);
        cherryEaten = true;
    }

    private void terrorBegin() {
        maze.spookyTimes(true);
        spookTimer = System.currentTimeMillis();
        for(Ghost g : maze.getGhosts()) {
            KeyCode randDir = getRandomDirection(g.getDirection());
            g.setDirection(randDir);
            g.setLastChangedCourse(System.currentTimeMillis());
        }
    }
}
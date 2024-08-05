package code.inventory;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import java.io.File;
import java.util.*;

public class Maze {
    private String map;
    private ArrayList<List<Block>> maze = new ArrayList<>();
    private ArrayList<Ghost> ghosts = new ArrayList<>();
    private ArrayList<Block> fruits = new ArrayList<>();
    private Pacman pacman;
    private Image wall;
    private Image space;
    private Image empty;
    private Image pacR;
    private Image pacL;
    private Image pacU;
    private Image pacD;
    private Image ghost1;
    private Image ghost2;
    private Image ghost3;
    private Image ghost4;
    private Image smallF;
    private Image bigF;
    private Image cherry;
    private Image clock;
    private Image spook;
    private String orientation;
    private boolean placeFruit = true;
    private long[] roundScore = {0, 0, 0};

    public Maze(String map) {
        this.map = map;

        if(map.equals("map1")) {
            wall = new Image("file:src/res/wall1.png");
        }
        if(map.equals("map2")) {
            wall = new Image("file:src/res/wall2.png");
        }
        if(map.equals("map3")) {
            wall = new Image("file:src/res/wall3.png");
        }
        if(map.equals("map4")) {
            wall = new Image("file:src/res/wall4.png");
        }
        space = new Image("file:src/res/space.png");
        empty = new Image("file:src/res/empty.png");
        pacR = new Image("file:src/res/pacman-right.png");
        pacL = new Image("file:src/res/pacman-left.png");
        pacU = new Image("file:src/res/pacman-up.png");
        pacD = new Image("file:src/res/pacman-down.png");
        ghost1 = new Image("file:src/res/ghost1.png");
        ghost2 = new Image("file:src/res/ghost2.png");
        ghost3 = new Image("file:src/res/ghost3.png");
        ghost4 = new Image("file:src/res/ghost4.png");
        smallF = new Image("file:src/res/small-f.png");
        bigF = new Image("file:src/res/big-f.png");
        cherry = new Image("file:src/res/cherry.png");
        clock = new Image("file:src/res/clock.png");
        spook = new Image("file:src/res/spook.png");

        ghosts.add(new Ghost("ghost", ghost1, 0, 0));
        ghosts.add(new Ghost("ghost", ghost2, 0, 0));
        ghosts.add(new Ghost("ghost", ghost3, 0, 0));
        ghosts.add(new Ghost("ghost", ghost4, 0, 0));

        File file = new File("src/code/" + map + ".txt");
        try {
            Scanner scanner = new Scanner(file);
            int i = 0;

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                List<Block> cells = new ArrayList<>();

                for(int j = 0; j < line.length(); j++) {
                    if(line.charAt(j) == 'W') {
                        cells.add(new Block("wall", wall, j*25, i*25));
                        continue;
                    }
                    if(line.charAt(j) == 'S') {
                        cells.add(new Block("space", space, j*25, i*25));
                        continue;
                    }
                    if(line.charAt(j) == 'G') {
                        cells.add(new Block("gate", space, j*25, i*25));
                        continue;
                    }
                    if(line.charAt(j) == '1') {
                        fruits.add(new Block("smallF", smallF, j*25+10.5, i*25+10.5));
                        cells.add(new Block("space1", space, j*25, i*25));
                        continue;
                    }
                    if(line.charAt(j) == '2') {
                        fruits.add(new Block("bigF", bigF, j*25+7.5, i*25+7.5));
                        cells.add(new Block("space2", space, j*25, i*25));
                        continue;
                    }
                    if(line.charAt(j) == 'P') {
                        cells.add(new Block("space", space, j*25, i*25));
                        pacman = new Pacman("pacman", pacR, j*25, i*25);
                        orientation = "right";
                        continue;
                    }
                    cells.add(new Block("empty", empty, j*25, i*25));
                }
                maze.add(cells);
                i++;
            }
            scanner.close();
        } catch(Exception e) {
            System.out.println(e);
        }

        setGhostPos();
    }

    public ArrayList<List<Block>> getMaze() {
        return maze;
    }

    public Pacman getPacman() {
        return pacman;
    }

    public ArrayList<Ghost> getGhosts() {
        return ghosts;
    }

    public ArrayList<Block> getFruits() {
        return fruits;
    }

    public void setPacmanFaceDir(KeyCode dir) {
        if(dir == KeyCode.RIGHT) {
            if(!orientation.equals("right")) {
                pacman.setImage(pacR);
                orientation = "right";
            }
        }

        if(dir == KeyCode.LEFT) {
            if(!orientation.equals("left")) {
                pacman.setImage(pacL);
                orientation = "left";
            }
        }

        if(dir == KeyCode.UP) {
            if(!orientation.equals("up")) {
                pacman.setImage(pacU);
                orientation = "up";
            }
        }

        if(dir == KeyCode.DOWN) {
            if(!orientation.equals("down")) {
                pacman.setImage(pacD);
                orientation = "down";
            }
        }
    }

    public void setGhostPos() {
        Set<String> usedPositions = new HashSet<>();
        Random random = new Random();

        for(Ghost g : ghosts) {
            boolean positionSet = false;
            while(!positionSet) {
                int i = random.nextInt(maze.size());
                int j = random.nextInt(maze.get(i).size());

                if(!maze.get(i).get(j).getType().equals("wall") && !maze.get(i).get(j).getType().equals("empty") && !maze.get(i).get(j).getType().equals("gate")) {
                    String position = i + "," + j;
                    if(!usedPositions.contains(position) && Math.sqrt((pacman.getPosX()-(j * 25))*(pacman.getPosX()-(j * 25)) +
                            (pacman.getPosY()-(i * 25))*(pacman.getPosY()-(i * 25))) > 150) {
                        usedPositions.add(position);
                        g.setPosX(j * 25);
                        g.setPosY(i * 25);
                        positionSet = true;
                    }
                }
            }
        }
    }

    public void setFruitPos(Pane pane) {
        Random random = new Random();
        boolean positionSet = false;

        while(!positionSet) {
            int i = random.nextInt(maze.size());
            int j = random.nextInt(maze.get(i).size());

            if(!maze.get(i).get(j).getType().equals("wall") && !maze.get(i).get(j).getType().equals("space1")
                    && !maze.get(i).get(j).getType().equals("space2") && !maze.get(i).get(j).getType().equals("empty") && !maze.get(i).get(j).getType().equals("gate")) {
                Block block = new Block("cherry", cherry, j*25+10.5, i*25+10.5);
                block.setPosX(j * 25);
                block.setPosY(i * 25);
                fruits.add(block);
                pane.getChildren().add(block.getImageView());
                positionSet = true;
            }
        }
    }

    public boolean getFruitStatus() {
        return placeFruit;
    }

    public void setFruitStatus(boolean b) {
        placeFruit = b;
    }

    public long[] getRoundScore() {
        return roundScore;
    }

    public void removeCherry(Pane pane) {
        Iterator<Block> it = fruits.iterator();

        while(it.hasNext()) {
            Block block = it.next();
            if(block.getType().equals("cherry")) {
                pane.getChildren().remove(block.getImageView());
                it.remove();
            }
            if(block.getType().equals("clock")) {
                pane.getChildren().remove(block.getImageView());
                it.remove();
            }
        }
    }

    public void setClock(Pane pane) {
        Random random = new Random();
        boolean positionSet = false;

        while(!positionSet) {
            int i = random.nextInt(maze.size());
            int j = random.nextInt(maze.get(i).size());

            if(!maze.get(i).get(j).getType().equals("wall") && !maze.get(i).get(j).getType().equals("space1")
                    && !maze.get(i).get(j).getType().equals("space2") && !maze.get(i).get(j).getType().equals("cherry")
                    && !maze.get(i).get(j).getType().equals("pacman") && !maze.get(i).get(j).getType().equals("empty") && !maze.get(i).get(j).getType().equals("gate")) {
                Block block = new Block("clock", clock, j*25+10.5, i*25+10.5);
                block.setPosX(j * 25);
                block.setPosY(i * 25);
                fruits.add(block);
                pane.getChildren().add(block.getImageView());
                positionSet = true;
            }
        }
    }

    public void spookyTimes(boolean b) {
        if(b) {
            for(Ghost g : ghosts) {
                g.setSpooky(true);
                g.setImage(spook);
                g.startChase(false);
            }
        }
        else {
            for(Ghost g : ghosts) {
                g.setSpooky(false);
                g.setInitialImage();
            }
        }
    }

    public String getMap() {
        return map;
    }
}
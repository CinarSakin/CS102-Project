package com.game;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Game{

    public static Stage currentStage;
    private static char saveslot;
    private Level level;
    private Hero hero;

    private long lastUpdate = 0;

    private final ArrayList<KeyCode> activeKeys = new ArrayList<>();

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (lastUpdate == 0) {
                lastUpdate = now;return;
            }

            double dt = (now - lastUpdate) / 1_000_000_000.0;
            lastUpdate = now;

            // Look at user input...
            handleInput();
            
            // update game
            level.update(dt);

            // draw everything
            renderGame();
        }
    };

    public Game(char aChar) {
        saveslot = aChar;
     }

    public void startGame(Stage aStage) {
        hero = Hero.getHero();
        currentStage = aStage;

        Scene scene = App.getScene();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (!activeKeys.contains(code)) {
                activeKeys.add(code);
            }
        });

        scene.setOnKeyReleased(event -> {
            activeKeys.remove(event.getCode());
        });

        for (App.GameLayer layer : App.GameLayer.values()) {
            Canvas c = App.getLayerCanvas(layer);
            c.widthProperty().bind(scene.widthProperty());
            c.heightProperty().bind(scene.heightProperty());
        }

        loadGame(saveslot);

        timer.start();
    }

    private void renderGame() {

        Point2D heroCenter = hero.getDimension().getCenter();
        double camX = -heroCenter.getX() + (App.getScene().getWidth() / 2);
        double camY = -heroCenter.getY() + (App.getScene().getHeight() / 2);

        for (App.GameLayer layer : App.GameLayer.values()) {
            // erasing old canvas and moving the "camera"
            GraphicsContext gc = App.getLayerGC(layer);
            Canvas c = App.getLayerCanvas(layer);
            gc.setTransform(1, 0, 0, 1, 0, 0);
            gc.clearRect(0, 0, c.getWidth(), c.getHeight());
            gc.save();
            gc.translate(camX, camY);
        }

        // drawing level containing entities and other objects
        level.draw();

        // moving camera back
        for (App.GameLayer layer : App.GameLayer.values()) {
            App.getLayerGC(layer).restore();
        }

        // TODO: drawing hud
        

    }

    private void handleInput() {
        if (hero == null) return;

        Point2D velocity = new Point2D(0, 0);
        if (activeKeys.contains(KeyCode.W)) velocity.add(0, -1);
        if (activeKeys.contains(KeyCode.A)) velocity.add(-1, 0);
        if (activeKeys.contains(KeyCode.S)) velocity.add(0, 1);
        if (activeKeys.contains(KeyCode.D)) velocity.add(1, 0);
        hero.move(velocity.normalize());
        
        if (activeKeys.contains(KeyCode.SPACE)) {
            hero.attack();
        }
    }

    public void stopGame() {
        timer.stop();
        lastUpdate = 0;
    }

    private void loadGame(char aSaveSlot) {
        // ToDo
        try {
            level = SaveManager.loadLevel(aSaveSlot);
        } catch (Exception e) {
            newGame(1);
        }
    }
    private void newGame(int levelCount) {
        // TODO
        level = Level.constructNew(levelCount);
    }

    public static boolean isInBounds(Entity a){
        ArrayList<Room> rooms = Level.getRooms();
        for(int i = 0 ; i< rooms.size() ;i++){
            Room b = rooms.get(i);
            Dimension roomDim = b.getDimension();
            if(a.getDimension().insideOf(roomDim)){return true;}
        }
        return false;
    }

    public static boolean isPositionValid(Entity a, double nextX, double nextY) {
        // Create a temporary dimension for where the entity wants to move
        Dimension nextPos = new Dimension(nextX, nextY, a.getDimension().getWidth(), a.getDimension().getHeight());

        // Check if this potential position is inside any Room or Hall
        for (Area area : Level.getAreas()) {
            if (nextPos.insideOf(area)) {
                return true;
            }
        }

        return false;
    }

}
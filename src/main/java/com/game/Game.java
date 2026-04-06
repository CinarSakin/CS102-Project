package com.game;

import java.util.ArrayList;
import java.util.Comparator;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public class Game{

    private static char saveslot;
    private Level level;
    public static Hero hero;

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
            Drawer.updateHUD();
        }
    };

    public Game(char aChar) {
        saveslot = aChar;
    }

    public void startGame() {

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

        Drawer.setupHUD();

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
        // drawer.draw(level);
    //    System.out.println(Level.getRooms());
        for (Room r : Level.getRooms()) {
            Drawer.draw(r);
        }
        for(Hall h : Room.getHHalls()){
            Drawer.draw(h,0);
        //    System.out.println(h.dim);
        }
        for(Hall h : Room.getVHalls()){
            Drawer.draw(h,1);
        }


        for (Area a : Level.getAreas()) {
            a.getEntities().sort(Comparator.comparingDouble(e -> e.getDimension().getY()));
            for (Entity e : a.getEntities()) {
                Drawer.draw(e);
            }
        }

        // moving camera back
        for (App.GameLayer layer : App.GameLayer.values()) {
            App.getLayerGC(layer).restore();
        }

        // TODO: drawing hud
        

    }

    private void handleInput() {
        if (hero == null) return;

        Point2D velocity = new Point2D(0, 0);
        if (activeKeys.contains(KeyCode.W)) velocity = velocity.add(0, -1);
        if (activeKeys.contains(KeyCode.A)) velocity = velocity.add(-1, 0);
        if (activeKeys.contains(KeyCode.S)) velocity = velocity.add(0, 1);
        if (activeKeys.contains(KeyCode.D)) velocity = velocity.add(1, 0);
        hero.move(velocity.normalize());
        
        if (activeKeys.contains(KeyCode.SPACE)) {
            hero.attack();
        }

        if (activeKeys.contains(GameSettings.getKeyCode("Interact"))) {
            boolean hasInteracted = false;

            for (Area area : Level.getAreas()) {
                ArrayList<Entity> entitiesCopy = new ArrayList<>(area.getEntities());
                for (Entity e : entitiesCopy) {
                    if (e instanceof WorldObject) {
                        WorldObject wo = (WorldObject) e;
                        if (wo.isHeroInRange()) {
                            wo.interact(); 
                            hasInteracted = true;
                            break;
                        }
                    }
                }
                if (hasInteracted) break;
            }
            if (hasInteracted) {
                activeKeys.remove(GameSettings.getKeyCode("Interact")); 
            }
        }
    }

    public void stopGame() {
        timer.stop();
        lastUpdate = 0;
    }

    private void loadGame(char aSaveSlot) {
        // ToDo
        try {
            level = Level.constructFromSave(aSaveSlot);
        } catch (Exception e) {
            newGame(1);
        }
    }

    public void saveCurrentGame() {
        if (this.level != null) {
            SaveManager.saveLevel(this.level, saveslot);
        }
    }

    private void newGame(int levelCount) {
        // TODO
        level = Level.constructNew(levelCount);
    }

}
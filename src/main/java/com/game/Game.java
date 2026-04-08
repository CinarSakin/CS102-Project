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

    private double lastMouseX;
    private double lastMouseY;
    private double cameraX;
    private double cameraY;    

    private static int levelCount = 1;
    private static char saveslot;
    public static Level level;
    public static Hero hero;
    public static int type = 0;//0 standard, 1 infinite

    public static boolean isPaused = false;
    private long lastUpdate = 0;

    public final ArrayList<KeyCode> activeKeys = new ArrayList<>();

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

    public void startInfinite(){
        startGame();
    }

    public void startGame() {

        Scene scene = App.getScene();

        scene.setOnMouseMoved(e -> {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
        });

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

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            boolean freshPress = !activeKeys.contains(code);
            if (freshPress) activeKeys.add(code);
            if (freshPress && code == GameSettings.getKeyCode("attack")) hero.attack();
            if (freshPress && code == GameSettings.getKeyCode("menu")) {
                HUD.closeMap();
                isPaused = !isPaused;
                if (isPaused) stopGame(0);
                else { timer.start(); App.closePauseMenu.run(); }
            }
            if (freshPress && code == GameSettings.getKeyCode("map") && !isPaused)
                HUD.toggleMap();
        });
    }

    public static void endGame(){
        App.drawGameEnding();
        SaveManager.deleteSave(saveslot);
    }

    private void renderGame() {

        Point2D heroCenter = hero.getDimension().getCenter();
        cameraX = -heroCenter.getX() + (App.getScene().getWidth() / 2);
        cameraY = -heroCenter.getY() + (App.getScene().getHeight() / 2);

        for (App.GameLayer layer : App.GameLayer.values()) {
            // erasing old canvas and moving the "camera"
            GraphicsContext gc = App.getLayerGC(layer);
            Canvas c = App.getLayerCanvas(layer);
            gc.setTransform(1, 0, 0, 1, 0, 0);
            gc.clearRect(0, 0, c.getWidth(), c.getHeight());
            gc.save();
            gc.translate(cameraX, cameraY);
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
            a.getEntities().sort(
                Comparator.comparingDouble(e -> e.getDimension().getBottomY())
            );
            for (Entity e : a.getEntities()) {
                Drawer.draw(e);
            }
        }

        Point2D mouseInWorld = getMouseWorldPosition();
        for (Area a : Level.getAreas()) {
            for (Entity e : a.getEntities()) {
                if (e instanceof WorldObject && e.getDimension().contains(mouseInWorld)) {
                    WorldObject w = (WorldObject) e;
                    if (w.interactable && w.isHeroInRange()) {
                        Drawer.drawHover((WorldObject)e);
                        break;
                    }
                }
            }
        }

        // moving camera back
        for (App.GameLayer layer : App.GameLayer.values()) {
            App.getLayerGC(layer).restore();
        }

        // drawing hud
        Drawer.updateHUD();

    }

    public Point2D getMouseWorldPosition() {
        double worldX = lastMouseX - cameraX;
        double worldY = lastMouseY - cameraY;
        return new Point2D(worldX, worldY);
    }

    private void handleInput() {
        if (hero == null) return;

        Point2D velocity = new Point2D(0, 0);
        if (activeKeys.contains(GameSettings.getKeyCode("up"))) velocity = velocity.add(0, -1);
        if (activeKeys.contains(GameSettings.getKeyCode("left"))) velocity = velocity.add(-1, 0);
        if (activeKeys.contains(GameSettings.getKeyCode("down"))) velocity = velocity.add(0, 1);
        if (activeKeys.contains(GameSettings.getKeyCode("right"))) velocity = velocity.add(1, 0);
        hero.moveByDirection(velocity.normalize());
        hero.setLastDirection(velocity);
        

        if (activeKeys.contains(GameSettings.getKeyCode("interact"))) {
            boolean hasInteracted = false;

            for (Area area : Level.getAreas()) {
                ArrayList<Entity> entitiesCopy = new ArrayList<>(area.getEntities());
                for (Entity e : entitiesCopy) {
                    if (e instanceof WorldObject) {
                        WorldObject w = (WorldObject) e;
                        if (w.interactable && w.isHeroInRange()) {
                            if (w.interact()) {
                                hasInteracted = true;
                                break;
                            }
                        }
                    }
                }
                if (hasInteracted) break;
            }
            if (hasInteracted) {
                activeKeys.remove(GameSettings.getKeyCode("interact")); 
            }
        }
          
    }

    public void stopGame(int i) {
        timer.stop();
        lastUpdate = 0;
        if(i == 0)App.drawPauseMenu();
    }

    public void unPauseTimer(){timer.start();}

    private void loadGame(char aSaveSlot) {
        App.gameOverShown = false;
        Level.resetLevel();
        try {
            level = Level.constructFromSave(aSaveSlot);
            System.out.println("[Load] Game loaded from slot: " + aSaveSlot);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("[Load] No save found for slot " + aSaveSlot + ", starting new game.");
            Level.resetLevel();
            newGame(1);
        } catch (Exception e) {
            System.err.println("[Load] Failed to load save (slot " + aSaveSlot + "): " + e.getMessage());
            e.printStackTrace();
            Level.resetLevel();
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

    public void setType(int gameType){this.type = gameType;}
    public static int getType(){return type;}

}
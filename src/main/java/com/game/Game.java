package com.game;

import java.lang.classfile.CodeBuilder.CatchBuilder;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.MoveTo;
import javafx.stage.Stage;

public class Game{

    public static Stage currentStage;
    private static char saveslot;
    private Level level;
    private Hero hero;
    public long lastUpdate;
    
    AnimationTimer timer = new AnimationTimer() {
    @Override
    public void handle(long now) {
        // 1. Girişleri kontrol et
        handleInput();

        // 2. Mantığı güncelle (Hareket, Çarpışma, Yapay Zeka)
        updateGame();

        // 3. Çizim yap
        renderGame(gc); 
    }
};

private void renderGame(GraphicsContext gc) {
    gc.clearRect(0, 0, width, height);

    for (Room room : level.getRooms()) {
        for (Entity e : room.getEntities()) {
            Drawer.draw(e);
        }
    }

    if (hero != null) hero.draw(gc);
}
    
    public Game(char aChar){saveslot = aChar;}

    public void startGame(Stage aStage){
        hero = Hero.getHero();
        currentStage = aStage;
        loadGame(saveslot);

        
        timer.handle(saveslot);

    }

    private void updateGame() {
        if (level == null) return;

        if (hero != null) {
            hero.update();
        }
      
        for (Room room : Level.getRooms()) {
            for (Entity entity : room.getEntities()) {
                entity.update();
            }
        }
    }

    private void handleInput() {
        if (hero == null) return;

        
        if (activeKeys.contains(KeyCode.W)) hero.move(1); // up
        if (activeKeys.contains(KeyCode.D)) hero.move(2); // right
        if (activeKeys.contains(KeyCode.S)) hero.move(3); // down
        if (activeKeys.contains(KeyCode.A)) hero.move(4); // left
        
        if (activeKeys.contains(KeyCode.SPACE)) {
            hero.attack();
        }
    }

    public void saveGame() {
        // ToDo
        Level.save(this.level);
    }

    private void loadGame(char aSaveSlot) {
        // ToDo
        try {
            level = Level.constructFromSave(aSaveSlot);
        } catch (Exception e) {
            newGame(1);
        }
    }
    private void newGame(int levelCount){
        //TODO
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

    public static Stage getStage(){ return currentStage;}
}
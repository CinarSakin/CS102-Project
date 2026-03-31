package com.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Game extends Application{

    private Level level;
    private Hero hero;

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int i = 1;
        hero = new Hero(null, null, i);
        level = Level.constructNew(i);

        // ToDo: update loop with JavaFX AnimationTimer
        do{
            
            render(gc, level);

            StackPane root = new StackPane(canvas);
            stage.setScene(new Scene(root));
            stage.setTitle("BSP Dungeon Generator");
            stage.show();
        }while(true);
        
    }

    public void saveGame() {
        // ToDo
    }

    private void loadGame(char saveSlot) {
        // ToDo
        Level.constructFromSave(saveSlot);        
    }


    
}

package com.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Game extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int i = 1;
        do{
            Level map = new Level(i);
            
            render(gc, map);

            StackPane root = new StackPane(canvas);
            stage.setScene(new Scene(root));
            stage.setTitle("BSP Dungeon Generator");
            stage.show();
        }while(true);
        
    }


    
}

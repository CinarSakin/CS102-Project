package com.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Drawer {
    public static void draw(Entity a){
        Dimension dim = a.getDimension();
        Image entityImage = a.getImage();
        Stage aStage = Game.getStage();
    }
    public static void draw(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.GRAY);
        gc.fillRect(dim.getX(), dim.getY(), dim.getWidth(), dim.getHeight());
        
        gc.setFill(javafx.scene.paint.Color.DARKSLATEGRAY);
        gc.fillRect(dim.getX(), dim.getY(), dim.getWidth(), 5);
    }
}

package com.game;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Drawer {
    public static void draw(Entity a){
        Dimension dim = a.getDimension();
        Image entityImage = a.getImage();
        Stage aStage = Game.getStage();
    }
}
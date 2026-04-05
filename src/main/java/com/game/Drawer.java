package com.game;

import javafx.scene.canvas.GraphicsContext;

public class Drawer {
    public static int gridSize = 18;
    public static void draw(Entity e){
        GraphicsContext gc;
        if(e instanceof Projectile.ProjectileType.BOMB){
            gc = App.getLayerGC(App.GameLayer.VFX);
        }else{
            gc = App.getLayerGC(App.GameLayer.ENTITIES);
        }
        gc.drawImage(e.getImage(), e.getDimension().getX(), e.getDimension().getY(),
         e.getDimension().getWidth(), e.getDimension().getHeight());
    }

    public static void draw(Room r){
        GraphicsContext gc = App.getLayerGC(App.GameLayer.GROUND);
        for(int i = (int)(r.getDimension().getX()); i < (r.getDimension().getWidth()); i += gridSize){
            for(int j = (int)(r.getDimension().getY()); j < (r.getDimension().getHeight()); j += gridSize){
                gc.drawImage(r.getImage(1), i, j, gridSize, gridSize);//change the image acordingly
            }
        }
        
    }

    public static void draw(Hall h, int type){
        GraphicsContext gc = App.getLayerGC(App.GameLayer.GROUND);
        if(type == 0){
            //horizontal case
        }else{
            //vertical case
        }
        for(int i = (int)(h.getDimension().getX()); i < (h.getDimension().getWidth()); i += gridSize){
            for(int j = (int)(h.getDimension().getY()); j < (h.getDimension().getHeight()); j += gridSize){
                gc.drawImage(h.getImage(1), i, j, gridSize, gridSize);//change the image acordingly
            }
        }
    }
}

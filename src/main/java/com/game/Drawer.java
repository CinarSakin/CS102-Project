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
        int f = 0;
        for(int i = (int)(r.getDimension().getX())-gridSize; i < (r.getDimension().getWidth())+gridSize; i += gridSize){
            for(int j = (int)(r.getDimension().getY())-gridSize; j < (r.getDimension().getHeight())+gridSize; j += gridSize){
                if(i == (int)(r.getDimension().getX())-gridSize && j != (int)(r.getDimension().getY())-gridSize)f = 1;
                else if(i == (r.getDimension().getWidth())+gridSize && j != (int)(r.getDimension().getY())-gridSize)f = 3;
                else if(j == (int)(r.getDimension().getY())-gridSize)f = 0;
                else if(j == (int)(r.getDimension().getHeight())+gridSize)f = 4;
                else f = 2;
                gc.drawImage(r.getImage(f), i, j, gridSize, gridSize);//change the image acordingly
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

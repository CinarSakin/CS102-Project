package com.game;

import com.game.App.GameLayer;
import com.game.Projectile.ProjectileType;

import javafx.scene.canvas.GraphicsContext;

public class Drawer {
    public static int gridSize = 18;
    public static void draw(Entity e){
        GraphicsContext gc;
        if (e instanceof Projectile && ((Projectile)e).getType() == ProjectileType.BOMB) {
            gc = App.getLayerGC(App.GameLayer.VFX);
        }else{
            gc = App.getLayerGC(App.GameLayer.ENTITIES);
        }
        gc.drawImage(e.getImage(), e.getDimension().getX(), e.getDimension().getY(),
         e.getDimension().getWidth(), e.getDimension().getHeight());
    }

    public static void draw(Room r){
        GraphicsContext gc = App.getLayerGC(GameLayer.GROUND);
        int f = 0;
        int cellAmountX = (int) (r.getDimension().getWidth() / gridSize);
        int cellAmountY = (int) (r.getDimension().getWidth() / gridSize);

        for (int i = 0; i < cellAmountX; i++){
            for (int j = 0; j < cellAmountY; j++){

                if (j == 0) f = 0;
                else if (j == cellAmountY - 1) f = 4;
                else if (i == 0) f = 1;
                else if (i == cellAmountX - 1) f = 3;
                else f = 2;
        
                double drawX = r.getDimension().getX() + (i * gridSize);
                double drawY = r.getDimension().getY() + (j * gridSize);
                gc.drawImage(r.getImage(f), drawX, drawY, gridSize, gridSize);//change the image acordingly

            }
        }
        
    }

    public static void draw(Hall h, int type){
        GraphicsContext gc = App.getLayerGC(GameLayer.GROUND);
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

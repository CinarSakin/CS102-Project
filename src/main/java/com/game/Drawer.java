package com.game;

import com.game.App.GameLayer;
import com.game.Projectile.ProjectileType;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Drawer {
    public static int gridSize = Level.gridSize;
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
        GraphicsContext gc;
        int f = 0;
        int cellAmountX = (int) (r.getDimension().getWidth() / gridSize);
        int cellAmountY = (int) (r.getDimension().getHeight() / gridSize);

        for (int i = -1; i <= cellAmountX; i++){
            for (int j = 0; j <= cellAmountY; j++){

                gc = App.getLayerGC(GameLayer.GROUND);

                if (j == cellAmountY) {
                    f = 0;
                    gc = App.getLayerGC(GameLayer.VFX);
                }
                else if (i == -1) f = 1;
                else if (i == cellAmountX) f = 1;
                else if (j == 0) f = 0;
                else f = 2;
        
                double drawX = r.getDimension().getX() + (i * gridSize);
                double drawY = r.getDimension().getY() + (j * gridSize);

                Image imgToDraw = Room.getImage(f);
                double rescale = gridSize / imgToDraw.getWidth();

                double height = imgToDraw.getHeight()*rescale;
                if (f != 2) {
                    drawY += (Room.getImage(1).getHeight() - Room.getImage(0).getHeight());
                }

                gc.drawImage(imgToDraw, drawX, drawY, gridSize, height);//change the image acordingly

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
            //    gc.drawImage(h.getImage(1), i, j, gridSize, gridSize);//change the image acordingly
            }
        }
    }
}

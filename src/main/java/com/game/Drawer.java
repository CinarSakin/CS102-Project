package com.game;

import com.game.App.GameLayer;
import com.game.Projectile.ProjectileType;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Drawer {
    public static int gridSize = Level.gridSize;
    private static double wallOffset = Area.getImage(1).getHeight() - Area.getImage(0).getHeight();

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
            for (int j = -1; j <= cellAmountY; j++){
 
                double drawX = r.getDimension().getX() + (i * gridSize);
                double drawY = r.getDimension().getY() + (j * gridSize);
 
                boolean isDoor = false;
                double cx = drawX + (gridSize / 2.0);
                double cy = drawY + (gridSize / 2.0);
                for (Hall h : Room.getHHalls()) {
                    if (cx >= h.getDimension().getX() && cx <= h.getDimension().getX() + h.getDimension().getWidth() &&
                        cy >= h.getDimension().getY() && cy <= h.getDimension().getY() + h.getDimension().getHeight()) {
                        isDoor = true; break;
                    }
                }
                for (Hall h : Room.getVHalls()) {
                    if (cx >= h.getDimension().getX() && cx <= h.getDimension().getX() + h.getDimension().getWidth() &&
                        cy >= h.getDimension().getY() && cy <= h.getDimension().getY() + h.getDimension().getHeight()) {
                        isDoor = true; break;
                    }
                }
 
                if (isDoor) continue;
 
                gc = App.getLayerGC(GameLayer.VFX);
 
                if (j == cellAmountY) f = 0;
                else if (i == -1) f = 1;
                else if (i == cellAmountX) f = 1;
                else if (j == -1) {
                    f = 0;
                    gc = App.getLayerGC(GameLayer.GROUND);
                }
                else {
                    f = 2;
                    gc = App.getLayerGC(GameLayer.GROUND);
                };
 
                Image imgToDraw = Room.getImage(f);
                double rescale = gridSize / imgToDraw.getWidth();
 
                double height = imgToDraw.getHeight()*rescale;
                if (f != 2) {
                    drawY += wallOffset;
                }
 
                gc.drawImage(imgToDraw, drawX, drawY, gridSize, height);
 
            }
        }
        
    }

    public static void draw(Hall h, int type){
        GraphicsContext ground = App.getLayerGC(GameLayer.GROUND);
        GraphicsContext vfx = App.getLayerGC(GameLayer.VFX);
        int cellAmountX = (int) (h.getDimension().getWidth() / gridSize);
        int cellAmountY = (int) (h.getDimension().getHeight() / gridSize);

        for (int i = 0; i < cellAmountX; i++){

            double drawX = h.getDimension().getX() + (i * gridSize);

            for (int j = 0; j < cellAmountY; j++){

                double drawY = h.getDimension().getY() + (j * gridSize);
                ground.drawImage(Area.getImage(2), drawX, drawY, gridSize, gridSize);

                if (j == 0 && type == 0) { //horizontal case
                    Image imgToDraw = Area.getImage(0);
                    double rescale = gridSize / imgToDraw.getWidth();
                    double height = imgToDraw.getHeight()*rescale;
                    ground.drawImage(imgToDraw, drawX, h.getDimension().getY() + wallOffset - gridSize, gridSize, height);
                    if (i != 0 && i != cellAmountX-1)
                        vfx.drawImage(imgToDraw, drawX, h.getDimension().getY() +cellAmountY*gridSize+wallOffset, gridSize, height);
                }
                
                if (i == 0 && type == 1) { //vertical case
                    Image imgToDraw = Area.getImage(1);
                    double rescale = gridSize / imgToDraw.getWidth();
                    double height = imgToDraw.getHeight()*rescale;
                    vfx.drawImage(imgToDraw, h.getDimension().getX()-gridSize, drawY+wallOffset, gridSize, height);
                    vfx.drawImage(imgToDraw, h.getDimension().getRightX(), drawY+wallOffset, gridSize, height);
                }

            }
        }
    }

}

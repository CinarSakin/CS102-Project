package com.game;

import com.game.App.GameLayer;
import com.game.Projectile.ProjectileType;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Drawer {
    public static int gridSize = Level.gridSize;
    private static double wallOffset = Area.getImage(1).getHeight() - Area.getImage(0).getHeight();

    public static void setupHUD() { HUD.setup(); }

    public static void updateHUD() { HUD.update(); }

    public static void draw(Entity e){
        GraphicsContext gc;

        if (e instanceof Projectile && ((Projectile)e).getType() == ProjectileType.BOMB){
            gc = App.getLayerGC(App.GameLayer.VFX);
        }else{
            gc = App.getLayerGC(App.GameLayer.ENTITIES);
        }

        gc.save();

        Image img = e.getImage();
        if (img == null) { gc.restore(); return; }

        double x = e.getDimension().getX();
        double y = e.getDimension().getY();
        double w = e.getDimension().getWidth();
        double h = e.getDimension().getHeight();

        if (e instanceof Projectile) {
            double angle = ((Projectile) e).getDrawAngle();
            double cx = x + w / 2;
            double cy = y + h / 2;
            gc.translate(cx, cy);
            gc.rotate(angle);
            gc.drawImage(img, -w / 2, -h / 2, w, h);
        } else if (e.isFlipped()) {
            gc.translate(x + w, y);
            gc.scale(-1, 1);
            gc.drawImage(img, 0, 0, w, h);
        } else {
            gc.drawImage(img, x, y, w, h);
        }

        gc.restore();

    }

    public static void drawHover(WorldObject w) {
        GraphicsContext gc = App.getLayerGC(GameLayer.VFX);
        Dimension d = w.getDimension();
        
        double x = d.getX() + d.getWidth() / 2;
        double y = d.getY() - 20;

        String actionText = "";
        String itemInfo = "";

        if (w instanceof Chest) {
            Chest chest = (Chest) w;
            if (!chest.open) {
                actionText = "[E] INTERACT";
                itemInfo = "Mystery Chest";
            }
        } 
        else if (w instanceof DroppedItem) {
            DroppedItem dropped = (DroppedItem) w;
            actionText = "[E] GATHER";
            itemInfo = dropped.item.name; // Item ismini gösterir
        }

        if (!actionText.isEmpty()) {
            drawInfoBox(gc, x, y, actionText, itemInfo);
        }
    }

    private static void drawInfoBox(GraphicsContext gc, double centerX, double bottomY, String action, String info) {
        gc.save();

        double pad = gridSize * 0.3;
        double lineH = gridSize * 0.45;
        double fontSize = gridSize * 0.5;
        double smallSize = gridSize * 0.45;

        int lines = info.isEmpty() ? 1 : 2;
        double width = Math.max(action.length(), info.length()) * fontSize * 0.32 + pad * 2;
        double height = lines * lineH + pad * 2;
        double x = centerX - width / 2;
        double y = bottomY - height - gridSize * 0.2;
        double r = gridSize * 0.2;

        gc.setFill(Color.rgb(20, 15, 45, 0.9));
        gc.fillRoundRect(x, y, width, height, r, r);
        gc.setStroke(Color.rgb(180, 160, 230, 0.9));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(x, y, width, height, r, r);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.translate(centerX, 0);
        gc.scale(0.8, 1);
        gc.translate(-centerX, 0);

        gc.setFont(javafx.scene.text.Font.font(App.fontPropSmall.get().getName(), fontSize));
        gc.setFill(Color.GOLD);
        gc.fillText(action, centerX, y + pad + lineH * 0.75);

        if (!info.isEmpty()) {
            gc.setFont(javafx.scene.text.Font.font(App.fontPropSmall.get().getName(), smallSize));
            gc.setFill(Color.rgb(210, 200, 240, 1));
            gc.fillText(info, centerX, y + pad + lineH * 1.75);
        }

        gc.restore();
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

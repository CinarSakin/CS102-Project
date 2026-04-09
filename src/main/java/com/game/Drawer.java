package com.game;

import java.util.ArrayList;
import java.util.Comparator;

import com.game.App.GameLayer;
import com.game.LivingEntity.LivingStateObject;
import com.game.LivingEntity.LivingStateObject.LivingState;
import com.game.Projectile.ProjectileType;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
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

        if (e instanceof Plate) {
            gc = App.getLayerGC(App.GameLayer.GROUND);
        }else{
            gc = App.getLayerGC(App.GameLayer.ENTITIES);
        }

        gc.save();

        if (e instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) e;
            if (le.currentStates != null) {
                for (LivingStateObject ls : le.currentStates) {
                    if (ls.state == LivingState.HEAL) {
                        Lighting greenTint = new Lighting();
                        greenTint.setSurfaceScale(0.0); 
                        greenTint.setDiffuseConstant(1.2); 
                        greenTint.setSpecularConstant(0.0); 
                        greenTint.setSpecularExponent(0.0); 
                        greenTint.setLight(new Light.Distant(45, 45, Color.rgb(148, 255, 171)));
                        gc.setEffect(greenTint);
                        break;
                    }
                    else if (ls.state == LivingState.TAKE_DAMAGE) {
                        Lighting redTint = new Lighting();
                        redTint.setSurfaceScale(0.0); 
                        redTint.setDiffuseConstant(1.2); 
                        redTint.setSpecularConstant(0.0); 
                        redTint.setSpecularExponent(0.0); 
                        redTint.setLight(new Light.Distant(45, 45, Color.rgb(255, 107, 107)));
                        gc.setEffect(redTint);
                        break;
                    }
                }
            }
        }

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

        // draw effects on top of entity
        if (e instanceof LivingEntity) {
            drawEffects((LivingEntity) e);
        }

    }

    private static long effectFrameTimer = 0;
    private static int effectFrame = 0;

    private static void drawEffects(LivingEntity le) {
        if (le.effects == null || le.effects.isEmpty()) return;

        long now = System.currentTimeMillis();
        if (now - effectFrameTimer > 300) {
            effectFrame = 1 - effectFrame;
            effectFrameTimer = now;
        }

        GraphicsContext gc = App.getLayerGC(App.GameLayer.ENTITIES);
        double w = le.getDimension().getWidth()*1.1;
        double h = le.getDimension().getHeight()*1.1;

        for (Effect effect : le.effects) {
            String spriteName = getEffectSprite(effect.getEffectType());
            if (spriteName == null) continue;
            Image img = AnimationManager.loadImage(spriteName, w, h);
            if (img == null) continue;
            gc.drawImage(img, le.getDimension().getX(), le.getDimension().getY(), w, h);
        }
    }

    private static String getEffectSprite(Effect.EffectType type) {
        int f = effectFrame + 1;
        switch (type) {
            case BURN:   return "effects/burn_effect" + f + ".png";
            case FREEZE: return "effects/freeze_effect" + f + ".png";
            case HEAL: return "effects/heal_effect" + f + ".png";
            case STUN:   return "effects/stun_effect.png";
            default: return null;
        }
    }

    public static void drawHover(WorldObject w) {
        GraphicsContext gc = App.getLayerGC(GameLayer.VFX);
        Dimension d = w.getDimension();
        
        double x = d.getX() + d.getWidth() / 2;
        double y = d.getY() - 20;

        if (!w.actionText.isEmpty()) {
            drawInfoBox(gc, x, y, "["+GameSettings.getKeyBinding("interact")+"] "+w.actionText, w.info);
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
 
                boolean transparent = gc == App.getLayerGC(GameLayer.VFX)
                    && isEntityBehindWall(drawX, drawY, gridSize, height, r);
                    
                if (transparent) gc.setGlobalAlpha(0.5);
                gc.drawImage(imgToDraw, drawX, drawY, gridSize, height);
                if (transparent) gc.setGlobalAlpha(1.0);
 
            }
        }
        
    }

    private static boolean isEntityBehindWall(double wallX, double wallY, double wallW, double wallH, Area area) {
        double checkTop = wallY;
        double checkBottom = wallY + wallH - gridSize;

        for (LivingEntity e : new ArrayList<>(area.getLivingEntities())) {
            Dimension d = e.getDimension();
            if (d.getRightX() > wallX && d.getX() < wallX + wallW
                    && d.getBottomY()>checkTop && d.getBottomY()-d.getHeight()*.3<checkBottom) {
                return true;
            }
        }
        return false;
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
                    if (i != 0 && i != cellAmountX-1) {
                        double wx = drawX, wy = h.getDimension().getY() + cellAmountY * gridSize + wallOffset;
                        boolean transparent = isEntityBehindWall(wx, wy, gridSize, height, h);
                        if (transparent) vfx.setGlobalAlpha(0.5);
                        vfx.drawImage(imgToDraw, wx, wy, gridSize, height);
                        if (transparent) vfx.setGlobalAlpha(1.0);
                    }
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

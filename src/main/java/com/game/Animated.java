package com.game;

import java.util.NoSuchElementException;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Animated {
    private final long ANIM_DELAY = 5000;

    private int noFrames;
    private Image spriteSheet;
    private ImageView sheetViewer;
    private AnimationTimer animTimer;
    
    Animated(Image spriteSheet) { // spriteSheet will have 1 column
        this.spriteSheet = spriteSheet;
        this.sheetViewer = new ImageView(spriteSheet);

        makeTimer();
    }

    private void makeTimer() {
        animTimer = new AnimationTimer(){
        long lastTime = 0;
        int x = 0; int y = 0;
        int frameWidth = (int) spriteSheet.getWidth()/noFrames;
        int frameHeight = (int) spriteSheet.getHeight();

        @Override
        public void handle(long now) {
            long deltaTime = now - lastTime;

            if (ANIM_DELAY < deltaTime) { // next frame 
                if (spriteSheet.getWidth() < x || spriteSheet.getHeight() < y) {
                    throw new NoSuchElementException("x and y out of spriteSheet bounds.");
                }

                sheetViewer.setViewport(new Rectangle2D(x, y, frameWidth, frameHeight));
                x += frameWidth;
                y += frameHeight;
            }
        }
    };
    }

    public static void startAnim(Animated anim) {
        anim.animTimer.start();
    }

    public void playAnim() {
        this.animTimer.start();
    }
    
    public void pauseAnim() {
        this.animTimer.stop();
    }

    public AnimationTimer getPlayer() {
        return animTimer;
    }
}

package com.game;

import java.util.NoSuchElementException;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Animation {
    /*
        1 | 1 | 1 | 1
        1 | 1 | 1 | 1
        1 | 1 | 1 | 1
        cols = 4
        rows = 3
    */
    public static final Image PLACEHOLDER_IMAGE = new Image("/sprites/ui/longBtnDefault.png");

    private final long ANIM_DELAY = 5000;

    private int noFrames;
    private Image spriteSheet;
    private ImageView sheetViewer;
    private AnimationTimer animTimer;

    enum AnimSpritesheets {
        // images are placeholders
        
        DIE(3, PLACEHOLDER_IMAGE),
        ATTACK(2, PLACEHOLDER_IMAGE),
        GET_BUFFED(2, PLACEHOLDER_IMAGE), // might be transparent
        WALKING_UP(1, PLACEHOLDER_IMAGE),
        WALKING_DOWN(1, PLACEHOLDER_IMAGE),
        WALKING_RIGHT(1, PLACEHOLDER_IMAGE),
        WALKING_LEFT(1, PLACEHOLDER_IMAGE),
        IDLE(1, PLACEHOLDER_IMAGE),
        TAKE_DAMAGE(0, PLACEHOLDER_IMAGE), // might be transparent
        ;

        private int noFrames;
        private Image spriteSheet;
        private AnimSpritesheets(int noFrames, Image spriteSheet) {
            this.noFrames = noFrames;
            this.spriteSheet = spriteSheet;
        }
    }

    public Animation(String LivingAnimStateName) {
        this(AnimSpritesheets.valueOf(LivingAnimStateName));
    }
    private Animation(AnimSpritesheets animSheets) { // spriteSheet will have 1 column
        this.noFrames = animSheets.noFrames;
        this.spriteSheet = animSheets.spriteSheet;
        this.sheetViewer = new ImageView(spriteSheet);

        makeTimer();
    }

    private void makeTimer() {
        animTimer = new AnimationTimer(){
        long lastTime = 0;
        int frameWidth = (int) spriteSheet.getWidth()/noFrames;
        int frameHeight = (int) spriteSheet.getHeight();
        int x = 0; int y = 0;

        @Override
        public void handle(long now) {
            long deltaTime = now - lastTime;

            if (ANIM_DELAY < deltaTime) { // next frame 
                if (spriteSheet.getWidth() < x || spriteSheet.getHeight() < y) {
                    throw new NoSuchElementException("x and y out of spriteSheet bounds.");
                }

                sheetViewer.setViewport(new Rectangle2D(x, y, now, frameHeight));
                x += frameWidth;
                y += frameHeight;
            }
        }
    };
    }

    public static void playAnim(String LivingAnimStateName) {
        Animation anim = new Animation(LivingAnimStateName);
        anim.animTimer.start();
    }

    public AnimationTimer getPlayer() {
        return animTimer;
    }
}

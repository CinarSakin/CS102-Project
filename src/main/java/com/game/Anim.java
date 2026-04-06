package com.game;

import javafx.scene.image.Image;

public class Anim {
    
    public int dx = 0;
    public int dy = 0;
    public int frameW, frameH;

    public int currentFrame;
    public int totalFrames;
    
    public boolean hasLoop;
    public Image[] frames;

    enum AnimStates {
        IDLE(true), WALKR(true), WALKL(true),
        FX(false), ATTACK(false), DIE(false)
        ;

        private boolean hasLoop;
        private AnimStates(boolean hasLoop) {
            this.hasLoop = hasLoop;
        }
    }

    enum Anims {
        // each enum is a spriteSheet, containing Image's
        // her frame için ayrı Image yapcaz başka çare yok

        HERO_IDLE(
            new Image("hero_idle")),
        HERO_WALKR(
            new Image("hero_idle")),
        HERO_WALKL(
            new Image("hero_idle_flipped")),
        HERO_ATTACK(
            new Image("hero_idle")),
        HERO_DIE(
            new Image("hero_idle")),

        WALKER_IDLE(
            new Image("monster")),
        WALKER_WALKR(
            new Image("monster")),
        WALKER_WALKL(
            new Image("monster")),
        WALKER_ATTACK(
            new Image("monster_attack")),
        WALKER_DIE(
            new Image("monster")),

        BOMBER_IDLE(
            new Image("monster")),
        BOMBER_WALKR(
            new Image("monster")),
        BOMBER_WALKL(
            new Image("monster")),
        BOMBER_ATTACK(
            new Image("monster_attack")),
        BOMBER_DIE(
            new Image("monster")),

        SKELETON_IDLE(
            new Image("monster")),
        SKELETON_WALKR(
            new Image("monster")),
        SKELETON_WALKL(
            new Image("monster")),
        SKELETON_ATTACK(
            new Image("monster_attack")),
        SKELETON_DIE(
            new Image("monster")),
        
        // particle effects
        // projectiles
        BOMB(
            new Image("talisman-1")), // explosion

        // effects
        STUN(
            new Image("talisman-1")),
        SLASH(
            new Image("talisman-1")),
        BURN(
            new Image("talisman-1")),
        FREEZE(
            new Image("talisman-1")),
        TIRE(
            new Image("talisman-1")),
        HEAL(
            new Image("talisman-1")),
        ;

        private int totalFrames;
        private Image[] frames;
        private Anims(Image... frames) {
            this.frames = frames;
            this.totalFrames = frames.length;
        }
    }

    public Anim(LivingEntity.LivingType lType, AnimStates animState) {
        this(Anims.valueOf(lType.name()+"_"+animState.name()), animState);
        this.hasLoop = animState.hasLoop;
    }
    public Anim(Effect.EffectType effeType) {
        this(Anims.valueOf(effeType.name()), AnimStates.FX);
    }
    public Anim(Projectile.ProjectileType pType) {
        this(Anims.valueOf(pType.name()), AnimStates.FX);
    }

    private Anim(Anims Anim, AnimStates animState) {
        this.frames = Anim.frames;
        this.hasLoop = animState.hasLoop;

        this.totalFrames = Anim.totalFrames;
        this.currentFrame = 0;
    }

    public Image nextFrame() {
        if (currentFrame < totalFrames) {
            currentFrame++;

            if (currentFrame == totalFrames && hasLoop) {
                currentFrame = 0;
            }
        }
        
        return frames[currentFrame];
    }
}

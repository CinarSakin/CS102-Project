package com.game;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.scene.image.Image;
import javafx.util.Duration;

public class AnimationManager {

    private int speed = 0;
    private LivingEntity livingEntity;
    
    // spriteSheet file names : hero_idle, walker_walk
    private AnimatedTexture baseLayer;
    private AnimatedTexture effectLayer;

    private AnimationChannel idleAnim, walkAnim, attackAnim, dieAnim, effectAnim;
    // Image spriteSheet, numFrames per row, single frame width, single frame height, duration of the animation channel, start frame and end frame

    enum Anims { // 0=idle, 1=walk, 2=attack, 3=die
        HERO(new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}),
        WALKER(new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}),
        BOMBER(new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}),
        SKELETON(new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}),

        // particle effects
        // projectiles
        BOMB(new int[]{1}, new int[]{2}), // explosion

        // effects
        STUN(new int[]{1}, new int[]{2}),
        SLASH(new int[]{1}, new int[]{2}),
        BURN(new int[]{1}, new int[]{2}),
        FREEZE(new int[]{1}, new int[]{2}),
        TIRE(new int[]{1}, new int[]{2}),
        HEAL(new int[]{1}, new int[]{2}),
        ;

        private int[] framesPerRow;
        private int[] durations;
        
        private Anims(int[] durations, int[] numFrames) {
            this.durations = durations;
            this.framesPerRow = numFrames;
        }
    }

    public AnimationManager(LivingEntity livingEntity) {
        Anims Anim = Anims.valueOf(livingEntity.lType.name());

        this.livingEntity = livingEntity;

        // placeholder values
        idleAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_idle"), Anim.framesPerRow[0], 128, 128, new Duration(Anim.durations[0]), 0, 3);
        walkAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_walk"), Anim.framesPerRow[1], 128, 128, new Duration(Anim.durations[1]), 0, 3);
        attackAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_attack"), Anim.framesPerRow[2], 128, 128, new Duration(Anim.durations[2]), 0, 3);
        dieAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_die"), Anim.framesPerRow[3], 128, 128, new Duration(Anim.durations[3]), 0, 3);

        baseLayer = new AnimatedTexture(idleAnim);
    }

    private AnimationManager(Anims Anim) {
        effectAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_fx"), Anim.framesPerRow[0], 128, 128, new Duration(Anim.durations[0]), 0, 3);
        effectLayer = new AnimatedTexture(effectAnim);
    }

    public AnimationManager(Projectile.ProjectileType projType) {
        this(Anims.valueOf(projType.name()));
    }
    public AnimationManager(Effect.EffectType effectType) {
        this(Anims.valueOf(effectType.name()));
    }

    // todo constructor and enums for world objects

    public void draw() {
        if (speed != 0) {
            if (baseLayer.getAnimationChannel() == idleAnim) {
                
                if (!livingEntity.isLookingRight || baseLayer.getScaleX() == -1) {
                    baseLayer.setScaleX(-1);
                }
                baseLayer.loopAnimationChannel(walkAnim);
            }
            speed = (int) (speed*0.9); // friction

            if (speed < 1) {
                speed = 0;
                baseLayer.loopAnimationChannel(idleAnim);
            }
        }
    }

    public void playAnim(LivingEntity.LivingStates state) { // for non-looping animations
        AnimationChannel animToPlay = idleAnim;
        switch (state.name()) {
            case "ATTACK" -> animToPlay = attackAnim;
            case "DIE" -> animToPlay = dieAnim;
        }

        baseLayer.playAnimationChannel(animToPlay);
    }

    public void playAnim(Effect.EffectType effectType) {
        effectLayer.playAnimationChannel(effectAnim);
    }

    public void playAnim(Projectile.ProjectileType projType) {
        effectLayer.playAnimationChannel(effectAnim);
    }
}

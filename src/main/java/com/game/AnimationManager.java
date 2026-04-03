package com.game;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.scene.image.Image;
import javafx.util.Duration;

public class AnimationManager {

    private int speed = 0;
    
    // spriteSheet file names : hero_idle, walker_walk
    private AnimatedTexture effectLayer;
    private AnimatedTexture baseLayer;

    private AnimationChannel idleAnim, walkAnim, attackAnim, dieAnim, effectAnim;
    // Image spriteSheet, numFrames per row, single frame width, single frame height, duration of the animation channel, start frame and end frame

    enum Anims { // 0=idle, 1=walk, 2=attack, 3=die
        HERO(new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}),
        WALKER(new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}),
        BOMBER(new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}),
        SKELETON(new int[]{1, 1, 1, 1}, new int[]{2, 2, 2, 2}),

        // particle effects
        STUN(new int[]{1}, new int[]{2} ),
        SLASH(new int[]{1}, new int[]{2}),
        BURN(new int[]{1}, new int[]{2}),
        FREEZE(new int[]{1}, new int[]{2}),
        TIRE(new int[]{1}, new int[]{2}),
        HEAL(new int[]{1}, new int[]{2}),
        ;

        private int[] numFrames;
        private int[] durations;
        
        private Anims(int[] durations, int[] numFrames) {
            this.durations = durations;
            this.numFrames = numFrames;
        }
    }

    public AnimationManager(LivingEntity.LivingType lType) {
        Anims Anim = Anims.valueOf(lType.name());

        idleAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_idle"), new Duration(Anim.durations[0]), Anim.numFrames[0]);
        walkAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_walk"), new Duration(Anim.durations[1]), Anim.numFrames[1]);
        attackAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_attack"), new Duration(Anim.durations[2]), Anim.numFrames[2]);
        dieAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_die"), new Duration(Anim.durations[3]), Anim.numFrames[3]);

        baseLayer = new AnimatedTexture(idleAnim);
    }
    
    public AnimationManager(Effect.EffectType effectType) {
        Anims Anim = Anims.valueOf(effectType.name());

        effectAnim = new AnimationChannel(new Image(Anim.name().toLowerCase()+"_fx"), new Duration(Anim.durations[0]), Anim.numFrames[0]);
        effectLayer = new AnimatedTexture(effectAnim);
    }

    // todo constructor and enums for world objects

    public void draw() {
        if (speed != 0) {
            if (baseLayer.getAnimationChannel() == idleAnim) {
                baseLayer.loopAnimationChannel(walkAnim);
            }
            speed = (int) (speed*0.9); // friction

            if (speed < 1) {
                speed = 0;
                baseLayer.loopAnimationChannel(walkAnim);
            }
        }
    }

    public void playAnim(LivingEntity.LivingStates state) { // for non-looping animations
        AnimationChannel animToPlay = idleAnim;
        switch (state.name()) {
            case "ATTACK":
                animToPlay = attackAnim;
                break;
            case "DIE":
                animToPlay = dieAnim;
                break;
        }

        baseLayer.playAnimationChannel(animToPlay);
    }

    public void playAnim(Effect.EffectType effectType) {
        effectLayer.playAnimationChannel(effectAnim);
    }
}

package com.game;

import com.game.Effect.EffectType;
import com.game.LivingEntity.LivingType;
import com.game.Projectile.ProjectileType;

import javafx.scene.image.Image;

public class AnimationManager {

    private static final int ANIM_DELAY = 500;

    private int speed = 0;
    private LivingEntity livingEntity;
    private Animats Animat;

    private Image imageToChange = livingEntity.getImage();

    private Anim currentAnim;
    private Anim effectAnim, idleAnim, walkRAnim, walkLAnim, attackAnim, dieAnim;

    enum Animats {
        HERO(
            new Anim(LivingType.HERO, Anim.AnimStates.IDLE),
            new Anim(LivingType.HERO, Anim.AnimStates.WALKR),
            new Anim(LivingType.HERO, Anim.AnimStates.WALKL),
            new Anim(LivingType.HERO, Anim.AnimStates.ATTACK),
            new Anim(LivingType.HERO, Anim.AnimStates.DIE)
        ),
        WALKER(
            new Anim(LivingType.WALKER, Anim.AnimStates.IDLE),
            new Anim(LivingType.WALKER, Anim.AnimStates.WALKR),
            new Anim(LivingType.WALKER, Anim.AnimStates.WALKL),
            new Anim(LivingType.WALKER, Anim.AnimStates.ATTACK),
            new Anim(LivingType.WALKER, Anim.AnimStates.DIE)
        ),
        BOMBER(
            new Anim(LivingType.HERO, Anim.AnimStates.IDLE),
            new Anim(LivingType.HERO, Anim.AnimStates.WALKR),
            new Anim(LivingType.HERO, Anim.AnimStates.WALKL),
            new Anim(LivingType.HERO, Anim.AnimStates.ATTACK),
            new Anim(LivingType.HERO, Anim.AnimStates.DIE)
        ),
        SKELETON(
            new Anim(LivingType.HERO, Anim.AnimStates.IDLE),
            new Anim(LivingType.HERO, Anim.AnimStates.WALKR),
            new Anim(LivingType.HERO, Anim.AnimStates.WALKL),
            new Anim(LivingType.HERO, Anim.AnimStates.ATTACK),
            new Anim(LivingType.HERO, Anim.AnimStates.DIE)
        ),
        
        // particle effects
        // projectiles
        BOMB(new Anim(ProjectileType.BOMB)), // explosion
        SLASH(new Anim(ProjectileType.SLASH)),

        // effects
        STUN(new Anim(EffectType.STUN)),
        BURN(new Anim(EffectType.BURN)),
        FREEZE(new Anim(EffectType.FREEZE)),
        TIRE(new Anim(EffectType.TIRE)),
        HEAL(new Anim(EffectType.HEAL)),
        ;
        
        private Anim[] animations;
        private Animats(Anim... animations) {
            this.animations = animations;
        }
    }

    public AnimationManager(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.Animat = Animats.valueOf(livingEntity.lType.name());

        idleAnim = Animat.animations[0];
        walkRAnim = Animat.animations[1];
        walkLAnim = Animat.animations[2];
        attackAnim = Animat.animations[3];
        dieAnim = Animat.animations[4];

        currentAnim = idleAnim;
    }

    private AnimationManager(Animats Anim) {
        effectAnim = Anim.animations[0];

        currentAnim = effectAnim;
    }

    public AnimationManager(Projectile.ProjectileType projType) {
        this(Animats.valueOf(projType.name()));
    }
    public AnimationManager(Effect.EffectType effectType) {
        this(Animats.valueOf(effectType.name()));
    }

    // todo constructor and enums for world objects

    public void draw(double dt) {
        if (speed != 0) {
            if (currentAnim == idleAnim) {
                
                if (!livingEntity.isLookingRight) {
                    currentAnim = walkRAnim;
                } else {
                    currentAnim = walkLAnim;
                }
            }
            speed = (int) (speed*0.9); // friction

            if (speed < 1) {
                speed = 0;
                currentAnim = idleAnim;
            }
        }

        if (dt > ANIM_DELAY) {
            imageToChange = currentAnim.nextFrame();
        }
    }

    public void setCurrentAnim(LivingEntity.LivingStates state) { // for non-looping animations
        Anim animToPlay = idleAnim;
        switch (state.name()) {
            case "ATTACK" -> animToPlay = attackAnim;
            case "DIE" -> animToPlay = dieAnim;
        }

        currentAnim = animToPlay;
    }

    public void setCurrentAnim(Effect.EffectType effectType) {
        currentAnim = Animats.valueOf(effectType.name()).animations[0];
    }

    public void setCurrentAnim(Projectile.ProjectileType projType) {
        currentAnim = Animats.valueOf(projType.name()).animations[0];
    }
}

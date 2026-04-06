package com.game;

import javafx.scene.image.Image;

public class Anim {
    public Image currentFrame;

    enum AnimStates { IDLE, WALKR, WALKL, FX, ATTACK, DIE }

    enum Anims {

        HERO_IDLE(
            new Image("hero_idle")),
        HERO_WALKR(
            new Image("hero_idle")),
        HERO_WALKL(
            new Image("hero_idle_flipped")),
        HERO_ATTACK(
            new Image("hero_attack")),
        HERO_DIE(
            new Image("hero_die")),

        WALKER_IDLE(
            new Image("monsterR")),
        WALKER_WALKR(
            new Image("monster_walkR")),
        WALKER_WALKL(
            new Image("monster_walkL")),
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
        private Image currentImage;
        private Anims(Image currentImage) {
            this.currentImage = currentImage;
        }
    }
    
    public Anim(LivingEntity.LivingType lType, AnimStates animState) {
        Anims anim = Anims.valueOf(lType.name()+"_"+animState.name());

        this.currentFrame = anim.currentImage;
    }

    public Anim(Effect.EffectType effeType) {
        this(Anims.valueOf(effeType.name()));
    }
    public Anim(Projectile.ProjectileType pType) {
        this(Anims.valueOf(pType.name()));
    }
    private Anim(Anims anim) {
        this.currentFrame = anim.currentImage; // anim.currentFrame de olur.
    }
}

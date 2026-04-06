package com.game;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.game.Anim.AnimStates;
import com.game.Effect.EffectType;
import com.game.LivingEntity.LivingStates;
import com.game.LivingEntity.LivingType;
import com.game.Projectile.ProjectileType;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

public class AnimationManager {

    private static final int FX = 0, IDLE = 0, WALK = 1, ATTACK = 2, DIE = 3;

    private int speed = 0;
    private LivingEntity livingEntity;
    private Animats Animat;

    private Image imageToChange = (WritableImage)livingEntity.getImage();

    private Image a = new Image("as", 100, 200, true, true);
    // drawImage(Image img, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh)
    // (s=source), (sx,sy) noktasından (sx+sw, sy+sh)'a kadar oluşan dikdörtgeni img resminden kırp,
    // (dx, dy) noktasından (dx+dw, dy+dh)'ye kadar oluşan dikdörtgene çiz.

    private Anim currentAnim;
    private Anim effectAnim, idleAnim, walkRAnim, walkLAnim, attackAnim, dieAnim;
    // Image spriteSheet, numFrames per row, single frame width, single frame height, duration of the animation channel, start frame and end frame

    enum Animats { // 0=fx, 0=idle, 1=walkRight, 2=walkLeft, 3=attack, 4=die
        // every anim has a spriteSheet
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

    public void draw() {
        if (speed != 0) {
            if (currentAnim == idleAnim) {
                
                if (!livingEntity.isLookingRight) {
                    currentAnim = walkRAnim;
                } else {

                }
            }
            speed = (int) (speed*0.9); // friction

            if (speed < 1) {
                speed = 0;
                currentAnim = idleAnim;
            }
        }

        imageToChange = currentAnim.nextFrame();
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

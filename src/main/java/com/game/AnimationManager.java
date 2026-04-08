package com.game;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.game.LivingEntity.LivingStateObject;

import javafx.scene.image.Image;

public class AnimationManager {

    private static final Map<String, Image> imageCache = new HashMap<>();

 //   private Animats Animat;
 //

    /*
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
    } */

    public static void updateImage(LivingEntity livingEntity) {
        double w = livingEntity.getDimension().getWidth();
        double h = livingEntity.getDimension().getHeight();

        if (livingEntity.currentStates == null) livingEntity.currentStates = new java.util.ArrayList<>();
        if (livingEntity.currentStates.isEmpty()) {
            livingEntity.imageToDraw = loadImage("entities/error.png",w ,h);
            return;
        }

        LivingStateObject highest = livingEntity.currentStates.stream()
            .max(Comparator.comparingInt(s -> s.state.ordinal()))
            .orElse(null);

        if (highest == null) {
            livingEntity.imageToDraw = loadImage("entities/error.png", w, h);
            return;
        }

        String stateName;
        double ratio = highest.getElapsedTime() / highest.state.animDuration;

        switch (highest.state) {
            case WALKING:
                if (ratio < 0.25) {
                    stateName = "walk1";
                } else if (ratio < 0.5) {
                    stateName = "idle";
                } else if (ratio < 0.75) {
                    stateName = "walk2";
                } else {
                    stateName = "idle";
                }
                break;
            default:
                stateName = highest.state.name().toLowerCase(Locale.ENGLISH);
                break;
        }

        livingEntity.imageToDraw = loadImage("entities/" + livingEntity.lType.name().toLowerCase(java.util.Locale.ENGLISH) + "_" + stateName + ".png", w, h);
    }

    public static Image loadImage(String name, double width, double height) {
        if (imageCache.containsKey(name)) return imageCache.get(name);

        var stream = AnimationManager.class.getResourceAsStream("/sprites/" + name);
        if (stream == null) stream = AnimationManager.class.getResourceAsStream("/sprites/entities/error.png");
        if (stream == null) return null;

        Image image = new Image(stream, width, height, false, true);
        imageCache.put(name, image);
        return image;
    }

}

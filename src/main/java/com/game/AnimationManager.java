package com.game;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.game.LivingEntity.LivingStateObject;

import javafx.scene.image.Image;

public class AnimationManager {

    private static final Map<String, Image> imageCache = new HashMap<>();

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
                    stateName = "idle2"; //walk1
                } else if (ratio < 0.5) {
                    stateName = "idle";
                } else if (ratio < 0.75) {
                    stateName = "idle3"; //walk2
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

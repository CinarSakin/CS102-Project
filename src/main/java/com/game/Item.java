package com.game;

import java.util.HashMap;
import javafx.scene.image.Image;

public abstract class Item {
    public transient Image image;
    private transient HashMap<Double, Image> sizedCache = new HashMap<>();
    public String imagePath;
    public String name;
    public String description;
    public double cooldownDuration;
    public double timeSinceLastUse;

    public Item(String imagePath, String name, String description, double cooldownDuration) {
        this.imagePath = imagePath;
        if (imagePath != null) {
            var stream = getClass().getResourceAsStream(imagePath);
            this.image = stream != null ? new Image(stream) : null;
        }
        this.name = name;
        this.description = description;
        this.cooldownDuration = cooldownDuration;
    }

    public Image loadImageAtSize(double size) {
        if (sizedCache == null) sizedCache = new HashMap<>();
        double key = Math.round(size);
        if (!sizedCache.containsKey(key)) {
            for (String path : new String[]{imagePath, "/sprites/entities/error.png"}) {
                if (path == null) continue;
                var stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    sizedCache.put(key, new Image(stream, key, key, true, false));
                    break;
                }
            }
        }
        return sizedCache.get(key);
    }

    public void updateTimer(double dt){
        timeSinceLastUse += dt;
    }

    public void resetTimer() {
        timeSinceLastUse = 0;
    }

    public boolean getIsOnCooldown() {
        return timeSinceLastUse < cooldownDuration;
    }

    public static Item randomItem(double luckFactor) {
        
        double categoryRoll = Math.random();

        if (categoryRoll <= .2) { // 20% -> weapon
            if (categoryRoll <= .09) {
                return Bow.randomBow(luckFactor); // 9% -> bow
            }
            return Sword.randomSword(luckFactor); // 11% -> sword
        } 
        
        else if (categoryRoll <= .5) { // 30% -> talisman
            return Talisman.randomTalisman(luckFactor);
        } 
        
        else { // 50% -> consumable
            return Consumable.randomConsumable(luckFactor);
        }
    }

}

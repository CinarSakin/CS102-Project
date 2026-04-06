package com.game;

import javafx.scene.image.Image;

public abstract class Item {
    public Image image; 
    public String name;
    public String description;
    public double cooldownDuration;
    private double cooldownTimer;
    private boolean isOnCooldown;

    public Item(Image image, String name, String description, double cooldownDuration) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.cooldownDuration = cooldownDuration;
    }

    public void updateCooldown(double time) {
        if (isOnCooldown) {
            cooldownTimer -= time;
            if (cooldownTimer <= 0) {
                cooldownTimer = 0;
                isOnCooldown = false;
            }
        }
    }

    private void startCooldown() {
        isOnCooldown = true;
        cooldownTimer = cooldownDuration;
    }

    public Effect applyEffect(LivingEntity target) {
        // ToDo
        return new Effect(Effect.EffectType.BURN, 0, target); // to silence the compiler
    }

    public double getCooldownTimer() {
        return cooldownTimer;
    }

    public boolean getIsOnCooldown() {
        return isOnCooldown;
    }

    public static Item randomItem(double luckFactor) {
        
        double categoryRoll = Math.random();
        double itemRoll = Math.random();

        if (categoryRoll <= .2) { // 20% -> weapon
            return Sword.randomSword(luckFactor);
        } 
        
        else if (categoryRoll <= .5) { // 30% -> talisman
            return Talisman.randomTalisman(luckFactor);
        } 
        
        else { // 50% -> consumable
            return Consumable.randomConsumable(luckFactor);
        }
    }

}

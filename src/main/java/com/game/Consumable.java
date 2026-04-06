package com.game;

import javafx.scene.image.Image;

public class Consumable extends Item {

    public enum ConsumableType {
        HEALTH_POTION(null, "Health Potion", "Regenerates your health.", 0.15),
        STRENGTH_POTION(null, "Strength Potion", "Powers you up.", 0.20),
        APPLE(null, "Apple", "Apple.", -1);

        public final Image image;
        public final String name;
        public final String description;
        public final double baseChance;

        ConsumableType(Image image, String name, String description, double baseChance) {
            this.image = image;
            this.name = name;
            this.description = description;
            this.baseChance = baseChance;
        }
    }

    private ConsumableType consumableType;

    public Consumable(ConsumableType type) {
        super(type.image, type.name, type.description, -1);
        this.consumableType = type;
    }

    public static Consumable randomConsumable(double luckFactor) {
        double roll = Math.random();
        double cumulativeChance = 0.0;

        for (ConsumableType type : ConsumableType.values()) {
            
            double actualChance = type.baseChance;

            if (type.baseChance != -1) {
                actualChance = type.baseChance * luckFactor;
            } else {
                actualChance = 1.0 - cumulativeChance; 
            }

            cumulativeChance += actualChance;

            if (roll <= cumulativeChance) {
                return new Consumable(type);
            }
        }
        
        return new Consumable(ConsumableType.APPLE); 
    }
    
}
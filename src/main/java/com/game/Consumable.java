package com.game;

import com.game.Effect.EffectType;

public class Consumable extends Item {

    public enum ConsumableType {
        HEALTH_POTION("/sprites/items/health_potion.png", "Health Potion", "Regenerates your health.", 0.15),
        STRENGTH_POTION("/sprites/items/strength_potion.png", "Strength Potion", "Powers you up.", 0.20),
        SPEED_POTION("/sprites/items/speed_potion.png", "Speed Potion", "Makes you faster.", 0.20),
        APPLE("/sprites/items/apple.png", "Apple", "Apple.", -1);

        public final String imagePath;
        public final String name;
        public final String description;
        public final double baseChance;

        ConsumableType(String imagePath, String name, String description, double baseChance) {
            this.imagePath = imagePath;
            this.name = name;
            this.description = description;
            this.baseChance = baseChance;
        }
    }

    private ConsumableType consumableType;

    public Consumable(ConsumableType type) {
        super(type.imagePath, type.name, type.description, -1);
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

    public void use(){
        if (this.consumableType == ConsumableType.APPLE) {
            Hero.getHero().heal(25);
        } else if (this.consumableType == ConsumableType.STRENGTH_POTION) {
            Hero.getHero().addEffect(EffectType.DMG_UP, 30);
        } else if (this.consumableType == ConsumableType.HEALTH_POTION) {
            Hero.getHero().addEffect(EffectType.HEAL, 10);
        } else { // starter sword

        }
    }
    
    public ConsumableType getConsumableType(){return this.consumableType;}
}
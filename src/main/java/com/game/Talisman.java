package com.game;

import javafx.scene.image.Image;

public class Talisman extends Item {
    
    public enum TalismanType {
        TALISMAN_5(new Image(Talisman.class.getResourceAsStream("/sprites/items/talismans/talisman-5.png")), "", "", 1, 0.05),
        TALISMAN_4(new Image(Talisman.class.getResourceAsStream("/sprites/items/talismans/talisman-4.png")), "", "", 1, 0.10),
        TALISMAN_3(new Image(Talisman.class.getResourceAsStream("/sprites/items/talismans/talisman-3.png")), "", "", 1, 0.20),
        TALISMAN_2(new Image(Talisman.class.getResourceAsStream("/sprites/items/talismans/talisman-2.png")), "", "", 1, 0.30),
        TALISMAN_1(new Image(Talisman.class.getResourceAsStream("/sprites/items/talismans/talisman-1.png")), "", "", 1, -1);

        public final Image image;
        public final String name;
        public final String description;
        public final double cooldownDuration;
        public final double baseChance;

        TalismanType(Image image, String name, String description, double cooldownDuration, double baseChance) {
            this.image = image;
            this.name = name;
            this.description = description;
            this.cooldownDuration = cooldownDuration;
            this.baseChance = baseChance;
        }
    }

    private TalismanType talismanType;

    public Talisman(TalismanType type) {
        super(type.image, type.name, type.description, type.cooldownDuration);
        this.talismanType = type;
    }

    public static Talisman randomTalisman(double luckFactor) {
        double roll = Math.random();
        double cumulativeChance = 0.0;

        for (TalismanType type : TalismanType.values()) {
            
            double actualChance = type.baseChance;

            if (type.baseChance != -1) {
                actualChance = type.baseChance * luckFactor;
            } else {
                actualChance = 1.0 - cumulativeChance; 
            }

            cumulativeChance += actualChance;

            if (roll <= cumulativeChance) {
                return new Talisman(type);
            }
        }
        
        return new Talisman(TalismanType.TALISMAN_1); 
    }
}
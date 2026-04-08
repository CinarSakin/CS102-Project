package com.game;

public class Talisman extends Item {
    
    public enum TalismanType {
        TALISMAN_5("/sprites/items/talisman-5.png", "Purple Gem", "Increases walk speed", 1, 0.10, 0, 0, 0, 0, 0.8),
        TALISMAN_4("/sprites/items/talisman-4.png", "Shadow Ring", "Boosts everything", 1, 0.02, 40, 10, 10, 0.4, 1),
        TALISMAN_3("/sprites/items/talisman-3.png", "Armor Ring", "Increases Armor", 1, 0.20, 0, 10, 0, 0, 0),
        TALISMAN_2("/sprites/items/talisman-2.png", "Blue Charm", "Increases damage and speed", 1, 0.30, 0, 0, 10, 0.2, 0),
        TALISMAN_1("/sprites/items/talisman-1.png", "Gold Ring", "Increases max health.", 1, 0.10, 25, 0, 0, 0, 0);

        public final String imagePath;
        public final String name;
        public final String description;
        public final double cooldownDuration;
        public final double baseChance;
        public final double healthBonus;
        public final double armorBonus;
        public final double damageBonus;
        public final double attackSpeedBonus;
        public final double walkSpeedBonus;

        TalismanType(String imagePath, String name, String description, double cooldownDuration, double baseChance, double healthBonus, double armorBonus, double damageBonus,
                     double attackSpeedBonus, double walkSpeedBonus) {
            this.imagePath = imagePath;
            this.name = name;
            this.description = description;
            this.cooldownDuration = cooldownDuration;
            this.baseChance = baseChance;
            this.healthBonus      = healthBonus;
            this.armorBonus       = armorBonus;
            this.damageBonus      = damageBonus;
            this.attackSpeedBonus = attackSpeedBonus;
            this.walkSpeedBonus   = walkSpeedBonus;
        }
    }

    private TalismanType talismanType;
    public boolean isEquipped = false;

    public Talisman(TalismanType type) {
        super(type.imagePath, type.name, type.description, type.cooldownDuration);
        this.talismanType = type;
    }

    public void equip(LivingEntity target){
        if(isEquipped) {
            return;
        }
        isEquipped = true;
        target.maxHealth   += talismanType.healthBonus;
        target.health      += talismanType.healthBonus;
        target.armor       += talismanType.armorBonus;
        target.damage      += talismanType.damageBonus;
        target.attackSpeed += talismanType.attackSpeedBonus;
        target.walkSpeed   += talismanType.walkSpeedBonus;
    }

    public void unequip(LivingEntity target){
        if(!isEquipped) {
            return;
        }
        isEquipped = false;
        target.maxHealth   -= talismanType.healthBonus;
        target.health       = Math.min(target.health, target.maxHealth); // if max health is lower than health
        target.armor       -= talismanType.armorBonus;
        target.damage      -= talismanType.damageBonus;
        target.attackSpeed -= talismanType.attackSpeedBonus;
        target.walkSpeed   -= talismanType.walkSpeedBonus;
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

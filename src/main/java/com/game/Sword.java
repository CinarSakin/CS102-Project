package com.game;

public class Sword extends Weapon {
    private SwordType swordType;

    public Sword(String name, String description, int attackSpeed, int damage, SwordType swordType) {
        super(name, description, ToolType.SWORD, attackSpeed, damage);
        this.swordType = swordType;
    }

    @Override
    public void use() {
        if (this.swordType == SwordType.FLAMING) {
            // ToDo
        }
    }

    @Override
    public void draw() {
        // ToDo
    }

    @Override
    public Effect applyEffect(LivingEntity target) { return null; }
}

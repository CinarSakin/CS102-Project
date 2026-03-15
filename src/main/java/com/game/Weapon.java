package com.game;

public abstract class Weapon extends Item {
    private ToolType type;
    private int attackSpeed;
    private int damage;
    public Weapon(String name, String description, ToolType type, int attackSpeed, int damage) {
        super(name, description);
        this.type = type;
        this.attackSpeed = attackSpeed;
        this.damage = damage;
    }
    public abstract void use();
}



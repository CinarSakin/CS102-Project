package com.game;

public abstract class Weapon extends Item {
    protected double attackSpeed;
    protected double damage;
    
    protected Weapon(String imagePath, String name, String description, double attackSpeed, double damage) {
        super(imagePath, name, description, 1.0/attackSpeed);
        this.attackSpeed = attackSpeed;
        this.damage = damage;
    }
    public abstract void use();
}



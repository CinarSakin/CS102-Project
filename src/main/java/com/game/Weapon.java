package com.game;

import javafx.scene.image.Image;

public abstract class Weapon extends Item {
    protected double attackSpeed;
    protected double damage;
    
    protected Weapon(Image image, String name, String description, double attackSpeed, double damage) {
        super(image, name, description, 1.0/attackSpeed);
        this.attackSpeed = attackSpeed;
        this.damage = damage;
    }
    public abstract void use();
}



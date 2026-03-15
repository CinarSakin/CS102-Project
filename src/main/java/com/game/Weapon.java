package com.game;

import javafx.scene.image.Image;

public abstract class Weapon extends Item {
    private double attackSpeed;
    private double damage;
    protected Weapon(Image image, String name, String description, double attackSpeed, double damage) {
        super(image, name, description);
        this.attackSpeed = attackSpeed;
        this.damage = damage;
    }
    public abstract void use();
}



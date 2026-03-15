package com.game;

import java.util.ArrayList;

public abstract class LivingEntity {
    public int maxHealth;
    public double health;
    public double armor;
    public double walkSpeed;
    public double attackSpeed;
    public double damage;
    public LivingType livingType; 
    public ArrayList<Effect> effects = new ArrayList<>();

    enum LivingType {
        HERO(100, 10, 10, 10, 10),      
        BOMBER(50, 5, 5, 5,20),
        SKELETON(75, 10, 1, 2,5); // add other enemy types

        private int maxHealth;
        private double health = maxHealth;
        private double armor;
        private double walkSpeed;
        private double attackSpeed;
        private double damage;

        private LivingType(int maxHealth, int armor, int damage, int walkSpeed, int attackSpeed) {
            this.maxHealth = maxHealth;
            this.armor = armor;
            this.damage = damage;
            this.attackSpeed = attackSpeed;
            this.walkSpeed = walkSpeed;
        }
    }

    public LivingEntity(int x, int y, LivingType livingType) {
        // super(posX, posY); // entity constructor

        this.maxHealth = livingType.maxHealth;
        this.health = livingType.maxHealth;
        this.armor = livingType.armor;
        this.damage = livingType.damage;
        this.walkSpeed = livingType.walkSpeed;
        this.attackSpeed = livingType.attackSpeed;
    }

    public void move(int dirx, int diry) {
        // todo
    }

    public void attack() {
        // todo
    }

    public void addHealth(int health) {
        this.health += health; // damages for - values, heals for + values
    }
}

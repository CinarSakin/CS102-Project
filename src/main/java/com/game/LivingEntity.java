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
        HERO(100, 0, 10, 10, 0.8),      
        BOMBER(50, 10, 20, 7,0.2),
        SKELETON(75, 5, 1, 8,0.3); // add other enemy types

        private int maxHealth;
        private double health;
        private double armor;
        private double walkSpeed;
        private double attackSpeed;
        private double damage;

        private LivingType(int maxHealth, double armor, double damage, double walkSpeed, double attackSpeed) {
            this.maxHealth = maxHealth;
            this.health = maxHealth;
            this.armor = armor;
            this.damage = damage;
            this.attackSpeed = attackSpeed;
            this.walkSpeed = walkSpeed;
        }
    }

    public LivingEntity(int x, int y, LivingType livingType, double diffMulti) {
        // super(posX, posY); // entity constructor

        this.maxHealth = (int)(livingType.maxHealth * diffMulti);
        this.health = this.maxHealth;
        this.armor = livingType.armor * diffMulti;
        this.damage = livingType.damage * diffMulti;
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

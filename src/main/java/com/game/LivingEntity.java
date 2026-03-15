package com.game;

import java.util.ArrayList;

public abstract class LivingEntity {
    protected int maxHealth;
    protected int currentHealth;
    protected int armor;
    protected int walkSpeed;
    protected int attackSpeed;
    protected int baseDamage;
    protected ArrayList<Effect> effects = new ArrayList<>();

    public LivingEntity(int x, int y, int maxHp, int armor, int baseDmg, int atkSpeed) {
        // super(posX, posY); entity constructor

        this.maxHealth = maxHp;
        this.currentHealth = maxHealth;
        this.armor = armor;
        this.baseDamage = baseDmg;
        this.attackSpeed = atkSpeed; 
    }

    public LivingEntity(int x, int y, LivingType LT) {
        // super(posX, posY); entity constructor
        int[] properties = new int[]{maxHealth, currentHealth, armor, walkSpeed, attackSpeed, baseDamage};

        for (int i = 0; i < properties.length; i++) {
            properties[i] = LT.getProperties()[i];
        }
    }

    public void move(int dirx, int diry) {
        /* posX += dirx;
        posY += diry; */
    }

    public void attack() {}

    public void addHealth(int hp) { currentHealth += hp; } // damages for - values, heals for + values
}

enum LivingType {
    HERO(100, 10, 10, 10),
    BOMBER(50, 5, 5, 20),
    SKELETON(75, 10, 1, 5); // TODO add other enemy types

    private int maxHealth;
    private int currentHealth;
    private int armor;
    private int walkSpeed;
    private int attackSpeed;
    private int baseDamage;
    
    private int[] properties = new int[]{maxHealth, currentHealth, armor, walkSpeed, attackSpeed, baseDamage};

    public int[] getProperties() {
        return properties;
    }

    private LivingType(int maxHp, int armor, int baseDmg, int atkSpeed) {
        this.maxHealth = maxHp;
        this.currentHealth = maxHealth;
        this.armor = armor;
        this.baseDamage = baseDmg;
        this.attackSpeed = atkSpeed;
    }
}

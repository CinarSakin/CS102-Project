package com.game;

import java.util.ArrayList;

import com.game.LivingEntity.AttackType;

public abstract class LivingEntity {
    // to suppress the compiler
    public int x;
    public int y;

    public int maxHealth;
    public double health;
    public double armor;
    public double damage;
    public double walkSpeed;
    public double attackSpeed;
    public int range; // distance enemies can attack from.
    public int fear; // chance of enemies running away in range when they get hit.
    public LivingType livingType;
    private AttackType attackType;
    public ArrayList<Effect> effects = new ArrayList<>();

    enum AttackType {
            MELEE, RANGED;
        }

    enum LivingType {
        HERO(100, 10, 10,   10, 10,   0, 0, null),
        WALKER(15, 0, 1, 10, 1,   10, 1, AttackType.MELEE),
        SKELETON(75, 10, 1,   2, 5,   1, 0, AttackType.MELEE),
        BOMBER(50, 5, 5,   5, 20,   20, 20, AttackType.RANGED);

        private int maxHealth;
        private double health = maxHealth;
        private double armor;
        private double damage;
        private double walkSpeed;
        private double attackSpeed;
        private int range;
        private int fear;
        private AttackType attackType;

        private LivingType(int maxHealth, int armor, int damage, int walkSpeed, int attackSpeed, int range, int fear, AttackType attackType) {
            this.maxHealth = maxHealth;
            this.armor = armor;
            this.damage = damage;
            this.walkSpeed = walkSpeed;
            this.attackSpeed = attackSpeed;
            this.range = range;
            this.fear = fear;
            this.attackType = attackType;
        }
    }

    public LivingEntity(int x, int y, LivingType livingType) {
        // super(x, y); entity constructor

        this.maxHealth = livingType.maxHealth;
        this.health = livingType.maxHealth;
        this.armor = livingType.armor;
        this.damage = livingType.damage;
        this.walkSpeed = livingType.walkSpeed;
        this.attackSpeed = livingType.attackSpeed;
        this.range = livingType.range;
        this.fear = livingType.fear;
        this.attackType = livingType.attackType;
    }
    
    public void move(int targetx, int targety) {
        x += Math.signum(x-targetx)*walkSpeed;
        y += Math.signum(y-targety)*walkSpeed;
    }

    public void attack(LivingEntity targetEntity) {
        if (targetEntity.attackType == AttackType.MELEE) {
            // todo
        }
        else if (targetEntity.attackType == AttackType.RANGED) {
            // todo
        }
    }

    public void changeHealth(int health) {
        this.health += health;
    }
}

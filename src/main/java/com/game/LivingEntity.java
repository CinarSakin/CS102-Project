package com.game;

import java.util.ArrayList;

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
    public double fear; // chance of enemies running away in range when they get hit.
    public LivingType livingType;
    public ArrayList<Effect> effects = new ArrayList<>();

    enum LivingType {
        HERO(100, 0, 10, 10, 0.8, 0){
            void update(){

            }
        },  
        BOMBER(50, 10, 20, 7, 0.2, 1){
            void update(){

            }
        },
        SKELETON(75, 5, 1, 8, 0.3, 1){
            void update(){

            }
        };
        // add other enemy types

        private int maxHealth;
        private double health;
        private double armor;
        private double damage;
        private double walkSpeed;
        private double attackSpeed;
        private double fear;

        private LivingType(int maxHealth, double armor, double damage, double walkSpeed, double attackSpeed, double fear) {
            this.maxHealth = maxHealth;
            this.health = maxHealth;
            this.armor = armor;
            this.damage = damage;
            this.walkSpeed = walkSpeed;
            this.attackSpeed = attackSpeed;
            this.fear = fear;
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
        this.fear = livingType.fear;
    }
    
    public void move(int targetx, int targety) {
        x += Math.signum(x-targetx)*walkSpeed;
        y += Math.signum(y-targety)*walkSpeed;
    }

    public void attack(LivingEntity targetEntity) {
        // todo
    }

    public void changeHealth(int health) {
        this.health += health;
    }
}

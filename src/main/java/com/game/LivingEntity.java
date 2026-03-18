package com.game;

import java.util.ArrayList;

import com.game.Effect.EffectType;
import com.game.Projectile.ProjectileType;

import javafx.geometry.Point2D;

public abstract class LivingEntity extends Entity {
    // to suppress the compiler
    public int maxHealth;
    public double health;
    public double armor;
    public double damage;
    public double walkSpeed;
    public double attackSpeed;
    public double range; // distance enemies can attack
    public double fear; // chance of enemies running away in range when they get hit.
    public LivingType livingType;
    public ArrayList<Effect> effects = new ArrayList<>();

    enum LivingType {
        HERO(new Point2D(48, 48), 100, 0, 10, 10, 0.8, 0, 0),
        BOMBER(new Point2D(36,36), 50, 10, 20, 7, 0.2, 1, 30),
        SKELETON(new Point2D(96, 96), 75, 5, 1, 8, 0.3, 1, 10)
        ;

        void attack(LivingEntity targetEntity) {}

        // add other enemy types
        private Point2D size;
        private int maxHealth;
        private double health;
        private double armor;
        private double damage;
        private double walkSpeed;
        private double attackSpeed;
        private double fear;
        private double range;

        private LivingType(Point2D size, int maxHealth, double armor, double damage, double walkSpeed, double attackSpeed, double fear, double range) {
            this.size = size;
            this.maxHealth = maxHealth;
            this.health = maxHealth;
            this.armor = armor;
            this.damage = damage;
            this.walkSpeed = walkSpeed;
            this.attackSpeed = attackSpeed;
            this.fear = fear;
            this.range = range;
        }
    }

    public LivingEntity(Point2D position, LivingType livingType, double diffMulti) {
        super(new Dimension(position.getX(), position.getY(), livingType.size.getX(), livingType.size.getY()) );

        this.maxHealth = (int)(livingType.maxHealth * diffMulti);
        this.health = this.maxHealth;
        this.armor = livingType.armor * diffMulti;
        this.damage = livingType.damage * diffMulti;
        this.walkSpeed = livingType.walkSpeed;
        this.attackSpeed = livingType.attackSpeed;
        this.fear = livingType.fear;
    }

    public void update() {
        for (Effect effe : effects) {
            if (effe.getRemainingDuration() < 0) {
                effects.remove(effe);
            }

            effe.affectEntity();
        }

        if (livingType.equals(LivingType.HERO)) {
            ((Hero)this).update();
        }
        else {
            ((Enemy)this).update();
        }
        draw();
    }

    public void attack() {}

    @Override
    public void draw() {} // todo

    public void heal(double healAmount) {
        this.health = Math.min(this.health+healAmount, this.maxHealth);
        // + heal animation
    }

    public void getDamaged(double damage){
        this.health = Math.max(this.health+damage, 0);

        if (this.health == 0){
            // if hero > lose the game
            // if enemy > despawn
        }
    }
}

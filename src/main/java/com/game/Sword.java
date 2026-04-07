package com.game;

import javafx.geometry.Point2D;
public class Sword extends Weapon {
    public static final Sword STARTER_SWORD = new Sword(SwordType.STARTER, 0);
    private SwordType swordType;

    public enum SwordType {
        FLAMING(
            "/sprites/items/flaming_sword.png",
            "Flaming Sword",
            "It Burns.",
            1.5,
            10,
            .1
        ),
        ICY(
            "/sprites/items/icy_sword.png",
            "Icy Sword",
            "Freezes the enemies!",
            1.5,
            10,
            .1
        ),
        NORMAL(
            "/sprites/items/icy_sword.png",
            "Sword",
            "Just a regular sword.",
            1,
            10,
            -1
        ),
        STARTER(
            "/sprites/items/icy_sword.png",
            "Starter Sword",
            "",
            1.5,
            10,
            1
        );

        public final String imagePath;
        public final String name;
        public final String description;
        public final double attackSpeed;
        public final double damage;
        public final double baseChance;
    
        SwordType(String imagePath, String name, String description, double attackSpeed, double damage, double baseChance) {
            this.imagePath = imagePath;
            this.name = name;
            this.description = description;
            this.attackSpeed = attackSpeed;
            this.damage = damage;
            this.baseChance = baseChance;
        }
    }

    public Sword(SwordType swordType, double damageMultiplier) {
        super(swordType.imagePath, swordType.name, swordType.description, swordType.attackSpeed, swordType.damage*damageMultiplier);
        this.swordType = swordType;
    }

    public static Sword randomSword(double luckFactor) {
        double roll = Math.random();
        double cumulativeChance = 0.0;

        for (SwordType type : SwordType.values()) {
            if(type!=SwordType.STARTER){
                double actualChance = type.baseChance;

                if (type.baseChance != -1) {
                    actualChance = type.baseChance * luckFactor;
                } else {
                    actualChance = 1.0 - cumulativeChance; 
                }

                cumulativeChance += actualChance;

                if (roll <= cumulativeChance) {
                    return new Sword(type, .75+Level.levelNo/4);
                }
            }
        }
        
        return new Sword(SwordType.NORMAL, .75+Level.levelNo/4); 
    }
    
    @Override
    public void use() {
        Dimension heroDim = Hero.getHero().getDimension();
        Point2D heroPos = heroDim.getPos();
        Dimension hitBox = new Dimension(heroPos.getX() + 20, heroPos.getY(), 30, 30);

        if (getIsOnCooldown()) {
            return;
        }

        for (LivingEntity target : Hero.getHero().currentArea.getLivingEntities()) {
            if (target != Hero.getHero() && target.getDimension().intersects(hitBox)) {
                target.getDamaged(this.damage);

                

                if (this.swordType == SwordType.FLAMING) {
                    new Projectile(Projectile.ProjectileType.SLASH, Projectile.TargetType.ENEMIES, heroPos, new Point2D(0, 0), attackSpeed, Hero.getHero().currentArea);
                    new Effect(Effect.EffectType.BURN, 3000, target).startEffect();
                } else if (this.swordType == SwordType.ICY) {
                    new Projectile(Projectile.ProjectileType.SLASH, Projectile.TargetType.ENEMIES, heroPos, new Point2D(0, 0), attackSpeed, Hero.getHero().currentArea);
                    new Effect(Effect.EffectType.FREEZE, 2000, target).startEffect();
                } else if (this.swordType == SwordType.NORMAL) {
                    new Projectile(Projectile.ProjectileType.SLASH, Projectile.TargetType.ENEMIES, heroPos, new Point2D(0, 0), attackSpeed, Hero.getHero().currentArea);
                    
                } else { // starter sword

                }
            }
        }
        startCooldown();
    }
}

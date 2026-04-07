package com.game;

import com.game.Projectile.TargetType;

import javafx.geometry.Point2D;
public class Sword extends Weapon {
    public static final Sword STARTER_SWORD = new Sword(SwordType.STARTER, 0);
    private SwordType swordType;

    public enum SwordType {
        FLAMING(
            "/sprites/items/flaming_sword.png",
            "Flaming Sword",
            "It Burns.",
            2.5,
            20,
            .1
        ),
        ICY(
            "/sprites/items/icy_sword.png",
            "Icy Sword",
            "Freezes the enemies!",
            3,
            15,
            .1
        ),
        NORMAL(
            "/sprites/items/icy_sword.png",
            "Sword",
            "Just a regular sword.",
            2,
            7.5,
            -1
        ),
        STARTER(
            "/sprites/items/icy_sword.png",
            "Starter Sword",
            "",
            1.5,
            5,
            0
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
        Point2D heroPos = Hero.getHero().getDimension().getPos();
    //    Dimension hitBox = new Dimension(heroPos.getX() + 20, heroPos.getY(), 30, 30);

        if (getIsOnCooldown()) {return;}

        //Point2D offset = new Point2D(Drawer.gridSize/2, 0);
        //Point2D slashPos = heroPos.add(Hero.getHero().isFlipped() ? offset.multiply(-1) : offset);
        
        double newX = Hero.getHero().lastDirrection.getX() >= 0 ? heroPos.getX()+ Drawer.gridSize/2: heroPos.getX()-Drawer.gridSize/2;
        double newY = Hero.getHero().lastDirrection.getY() >= 0 ? heroPos.getY()+ Drawer.gridSize/2: heroPos.getY()-Drawer.gridSize/2;

        Point2D slashPos = new Point2D(newX, newY);
        new Projectile(
            Projectile.ProjectileType.SLASH, TargetType.ENEMIES, slashPos,
                Point2D.ZERO, attackSpeed, Hero.getHero().currentArea
        );

        /*
        ArrayList<LivingEntity> a = new ArrayList<>(Hero.getHero().currentArea.getLivingEntities());
        for (LivingEntity target : a) {
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
        } */

        resetTimer();
    }
}

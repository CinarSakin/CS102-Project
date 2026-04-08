package com.game;

import java.util.ArrayList;

import com.game.Projectile.TargetType;

import javafx.geometry.Point2D;

public class Bow extends Weapon{
    public static final Bow STARTER_SWORD = new Bow(BowType.STARTER, 0);
    private BowType bowType;

    public enum BowType {
        FLAMING(
            "/sprites/items/bow.png",
            "Flaming Sword",
            "It Burns.",
            5,
            12,
            .1
        ),
        NORMAL(
            "/sprites/items/bow.png",
            "Bow",
            "Just a regular sword.",
            5,
            8,
            -1
        ),
        STARTER(
            "/sprites/items/bow.png",
            "Starter Sword",
            "",
            4.5,
            7,
            0
        );

        public final String imagePath;
        public final String name;
        public final String description;
        public final double attackSpeed;
        public final double damage;
        public final double baseChance;
    
        BowType(String imagePath, String name, String description, double attackSpeed, double damage, double baseChance) {
            this.imagePath = imagePath;
            this.name = name;
            this.description = description;
            this.attackSpeed = attackSpeed;
            this.damage = damage;
            this.baseChance = baseChance;
        }
    }

    public Bow(BowType swordType, double damageMultiplier) {
        super(swordType.imagePath, swordType.name, swordType.description, swordType.attackSpeed, swordType.damage*damageMultiplier);
        this.bowType = swordType;
    }

    public static Bow randomSword(double luckFactor) {
        double roll = Math.random();
        double cumulativeChance = 0.0;

        for (BowType type : BowType.values()) {
            if(type!=BowType.STARTER){
                double actualChance = type.baseChance;

                if (type.baseChance != -1) {
                    actualChance = type.baseChance * luckFactor;
                } else {
                    actualChance = 1.0 - cumulativeChance; 
                }

                cumulativeChance += actualChance;

                if (roll <= cumulativeChance) {
                    return new Bow(type, .75+Level.levelNo/4);
                }
            }
        }
        
        return new Bow(BowType.NORMAL, .75+Level.levelNo/4); 
    }
    
    @Override
    public void use() {
        Point2D heroPos = Hero.getHero().getDimension().getCenter();
    //    Dimension hitBox = new Dimension(heroPos.getX() + 20, heroPos.getY(), 30, 30);

        if (getIsOnCooldown()) {return;}

        //Point2D offset = new Point2D(Drawer.gridSize/2, 0);
        //Point2D slashPos = heroPos.add(Hero.getHero().isFlipped() ? offset.multiply(-1) : offset);
        
        Point2D lastDir = Hero.getHero().lastDirection.multiply(.75);
        double offsetX = lastDir.getX()>0 ? Drawer.gridSize : (lastDir.getX()<0 ? -Drawer.gridSize : 0);
        double offsetY = lastDir.getY()>0 ? Drawer.gridSize : (lastDir.getY()<0 ? -Drawer.gridSize : 0);

        Point2D slashPos = heroPos.add(new Point2D(offsetX, offsetY));
        Projectile p = new Projectile(
            Projectile.ProjectileType.ARROW, TargetType.ENEMIES, slashPos, Point2D.ZERO, attackSpeed, Hero.getHero().currentArea
        );
        p.dimension.moveCenterTo(slashPos);

        ArrayList<LivingEntity> a = new ArrayList<>(Hero.getHero().currentArea.getLivingEntities());
        for (LivingEntity target : a) {
            if (target != Hero.getHero() && target.getDimension().intersects(p.getDimension())) {
                

                if (this.bowType == BowType.FLAMING) {
                    new Effect(Effect.EffectType.BURN, 3000, target).startEffect();
                } else if (this.bowType == BowType.ICY) {
                    new Effect(Effect.EffectType.FREEZE, 2000, target).startEffect();
                } else if (this.bowType == BowType.NORMAL) {
                    
                } else { // starter sword

                }
            }
        }

        resetTimer();
    }
}

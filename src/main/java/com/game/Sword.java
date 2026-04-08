package com.game;

import java.util.ArrayList;

import com.game.Projectile.TargetType;

import javafx.geometry.Point2D;
public class Sword extends Weapon {
    public static final Sword STARTER_SWORD = new Sword(SwordType.STARTER, 2);
    public SwordType swordType;

    public enum SwordType {
        FLAMING(
            "/sprites/items/flaming_sword.png",
            "Flaming Sword",
            "It Burns.",
            5,
            12,
            .1
        ),
        ICY(
            "/sprites/items/icy_sword.png",
            "Icy Sword",
            "Freezes the enemies!",
            5.5,
            10,
            .1
        ),
        NORMAL(
            "/sprites/items/sword.png",
            "Sword",
            "Just a regular sword.",
            5,
            8,
            -1
        ),
        STARTER(
            "/sprites/items/starter_sword.png",
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
        Point2D heroPos = Hero.getHero().getDimension().getCenter();
    //    Dimension hitBox = new Dimension(heroPos.getX() + 20, heroPos.getY(), 30, 30);

        if (getIsOnCooldown()) {return;}

        //Point2D offset = new Point2D(Drawer.gridSize/2, 0);
        //Point2D slashPos = heroPos.add(Hero.getHero().isFlipped() ? offset.multiply(-1) : offset);
        
        Point2D lastDir = Hero.getHero().lastDirection.multiply(.75);
        double offsetX = lastDir.getX()>0 ? Drawer.gridSize : (lastDir.getX()<0 ? -Drawer.gridSize : 0);
        double offsetY = lastDir.getY()>0 ? Drawer.gridSize : (lastDir.getY()<0 ? -Drawer.gridSize : 0);
        Point2D offset = new Point2D(offsetX, offsetY);

        Projectile p = new Projectile(
            Projectile.ProjectileType.SLASH, TargetType.ENEMIES, Point2D.ZERO, offset, attackSpeed, Hero.getHero().currentArea
        );
        p.dimension.moveCenterTo(heroPos.add(offset.multiply(.4)));

        resetTimer();
    }
}

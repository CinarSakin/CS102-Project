package com.game;

import com.game.Projectile.ProjectileType;
import com.game.Projectile.TargetType;

import javafx.geometry.Point2D;

public class Bow extends Weapon{
    private BowType bowType;

    public enum BowType {
        FLAMING(
            "/sprites/items/bow.png",
            "Flaming Sword",
            "It Burns.",
            4,
            12,
            .1
        ),
        ICY(
            "/sprites/items/bow.png",
            "Icy Sword",
            "Freezes the enemies.",
            3.7,
            15,
            .1
        ),
        NORMAL(
            "/sprites/items/bow.png",
            "Bow",
            "Just a regular sword.",
            3.5,
            8,
            -1
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

    public static Bow randomBow(double luckFactor) {
        double roll = Math.random();
        double cumulativeChance = 0.0;

        for (BowType type : BowType.values()) {
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
        
        return new Bow(BowType.NORMAL, .75+Level.levelNo/4); 
    }
    
    @Override
    public void use() {
        if (getIsOnCooldown()) {return;}

        Point2D lastDir = Hero.getHero().lastDirection.multiply(.5);
        double offsetX = lastDir.getX()>0 ? Drawer.gridSize : (lastDir.getX()<0 ? -Drawer.gridSize : 0);
        double offsetY = lastDir.getY()>0 ? Drawer.gridSize : (lastDir.getY()<0 ? -Drawer.gridSize : 0);

        ProjectileType projType = 
            bowType == BowType.NORMAL ? ProjectileType.ARROW :
            bowType == BowType.FLAMING ? ProjectileType.FLAMING_ARROW :
            ProjectileType.ICY_ARROW;

        Point2D pos = Hero.getHero().getDimension().getCenter().add(new Point2D(offsetX, offsetY));
        Projectile p = new Projectile(projType, TargetType.ENEMIES, pos, lastDir, 1, Hero.getHero().currentArea);
        p.dimension.moveCenterTo(pos);

        resetTimer();
    }
}

package com.game;

import com.game.Projectile.ProjectileType;
import com.game.Projectile.TargetType;

import javafx.geometry.Point2D;

public class Boss extends Enemy {
    
    private static final int ATTACK_PROJECTILE_NUMBER = 3;

    public Boss(Point2D position, Area currentArea, double diffMulti) {
        super(LivingEntity.LivingType.BOSS, position, currentArea, diffMulti);
        imageToDraw = AnimationManager.loadImage(
            "entities/boss.png",
            getDimension().getWidth(),
            getDimension().getHeight()
        );
    }

    @Override
    public void attack() {
        double rand = Math.random();

        if (rand < 0.5)
            shotsAttack();

        else
            orbAttack();
        
    }

    public void shotsAttack() {

        Point2D direction = findTargetDirection(Game.hero);
        
        double baseAngle = Math.atan2(direction.getY(), direction.getX()); 
        double offset = Math.toRadians(15); // spawns two more arrows that are 15 degrees apart from the first arrow

        Point2D dirLeft   = new Point2D(Math.cos(baseAngle - offset), Math.sin(baseAngle - offset));
        Point2D dirRight  = new Point2D(Math.cos(baseAngle + offset), Math.sin(baseAngle + offset));

        new Projectile(ProjectileType.ARROW, TargetType.HERO, dimension.getPos(), direction, 1.5, currentArea);
        new Projectile(ProjectileType.ARROW, TargetType.HERO, dimension.getPos(), dirLeft, 1.5, currentArea);
        new Projectile(ProjectileType.ARROW, TargetType.HERO, dimension.getPos(), dirRight, 1.5, currentArea);

    }

    public void orbAttack() {
        Point2D direction = findTargetDirection(Hero.getHero());

        new Projectile(Projectile.ProjectileType.BOSS_ORB, Projectile.TargetType.HERO, dimension.getPos(), direction, 1, currentArea);
    }

    /*
    \     /\
     \     /
      \___/
    */
    public void ramAttack() { // spiral şeklinde hareket ediyo 
        boolean goNegative = false;
        double distancex = 1;
        double distancey = 1;

        for (int i = 0; i < 10; i++) {
            int j = -i;

            this.moveBy(i, j);
            this.moveBy(i, -j);
        }
    }
}

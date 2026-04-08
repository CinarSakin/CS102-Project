package com.game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Boss extends Enemy {
    
    private static final long ATTACK_DELAY = 1000;
    private static final int ATTACK_PROJECTILE_NUMBER = 3;
    private Image bossImage = loadImage("entities/error.png", 64, 64);

    public Boss(Point2D position, Area currentArea, double diffMulti) {
        super(LivingEntity.LivingType.BOSS, position, currentArea, diffMulti);
        System.out.println("ben buranın hakimi");
        imageToDraw = bossImage;
    }

    @Override
    public void attack() {
        double rand = Math.random();

        if (rand < 0.25) {
            shotsAttack();
        }
        else if (rand < 0.5) { 
            orbAttack();
        }
        else if (rand < 0.75) {
            ramAttack();
        }
    }

    public void shotsAttack() {
        Point2D targetPos = Hero.getHero().dimension.getPos();
        Point2D direction;

        for (int i = ATTACK_PROJECTILE_NUMBER/2; i < ATTACK_PROJECTILE_NUMBER; i++) {
            direction = targetPos.subtract(this.dimension.getPos().subtract(new Point2D(0, i))).normalize();

            new Projectile(Projectile.ProjectileType.ARROW, Projectile.TargetType.HERO, dimension.getPos(), direction, 2, currentArea);
        }
    }

    public void orbAttack() {
        Point2D direction = findTargetDirection(Hero.getHero());

        for (int i = 0; i < ATTACK_PROJECTILE_NUMBER; i++) {
            new Projectile(Projectile.ProjectileType.BOSS_ORB, Projectile.TargetType.HERO, dimension.getPos(), direction, 2, currentArea);
        }
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

            this.move(i, j);
            this.move(i, -j);
        }
    }
}

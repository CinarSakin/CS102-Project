package com.game;

import com.game.Effect.EffectType;
import com.game.Projectile.ProjectileType;
import com.game.Projectile.TargetType;

import javafx.geometry.Point2D;

public class Enemy extends LivingEntity {
    
    public Enemy(LivingType lType, Point2D position, Area currentArea, double diffMulti) {
        super(lType, position, currentArea, diffMulti);
    }
    // @Override // if you can implement like this it will be easier.
    // public Enemy(int type, Point2D pos,LivingEntity tarEntity, double diffMulti){

    // }
    
    @Override
    public void update(double dt) {
        if (!inAttackRange() && inRange()) {
            super.follow(Hero.getHero());
        } else {
            if (canAttack() && inAttackRange()) {
                attack();
                resetAttack();
            }
        }

        super.update(dt);
    }

    @Override
    public void attack() {
        Point2D direction = Hero.getHero().dimension.getPos().subtract(this.dimension.getPos());
        
        switch (super.getLivingType()) {
            case BOMBER:
                new Projectile(ProjectileType.BOMB, TargetType.ALL, dimension.getPos(), direction, 1, currentArea);
                break;
            case SKELETON:
                new Projectile(ProjectileType.ARROW, TargetType.HERO, dimension.getPos(), direction, 1, currentArea);
                break;
            case WALKER:
                new Projectile(ProjectileType.SLASH, TargetType.HERO, Hero.getHero().getDimension().getPos(), Point2D.ZERO, 1, currentArea);
                break;
        }
    }

    public void flee() {
        Point2D direction = findTargetDirection(Hero.getHero()).multiply(-1);
        moveByDirection(direction);
    }

    @Override
    public void getDamaged(double damage) {
        super.getDamaged(damage);
        if (0.15 > Math.random()*fear && inAttackRange()) {
            this.setEffect(EffectType.FEAR, 1.5);
        }
    }
    
    @Override
    public void reloadImages() {
        AnimationManager.updateImage(this);
    }

    public boolean inRange(){
        return (getDimension().getCenter().distance(Hero.getHero().getDimension().getCenter())<range);
    }
    public boolean inAttackRange() {
        if(Hero.getHero() != null)
        return dimension.getCenter().distance(Hero.getHero().dimension.getCenter()) < attackRange;
        return false;
    }
}

package com.game;

import com.game.Effect.EffectType;
import com.game.Projectile.ProjectileType;

import javafx.geometry.Point2D;

public class Enemy extends LivingEntity {
    private LivingEntity targetEntity;
    private boolean canAttack = true;

    public Enemy(LivingType LT, Point2D position, LivingEntity targetEntity, double diffMulti) {
        super(LT, position, 1);
        this.targetEntity = targetEntity;
    }
    // @Override // if you can implement like this it will be easier.
    // public Enemy(int type, Point2D pos,LivingEntity tarEntity, double diffMulti){

    // }
    
    @Override
    public void update() {
        Point2D position = new Point2D(dimension.getX(), dimension.getY());

        if (range < position.distance(targetEntity.dimension.getPos())) {
            follow();
        }
        else {
            if (canAttack) {
                attack();
                new Effect(EffectType.TIRE, ((long)attackSpeed)*1, this).startEffect(); // cooldown
            }
        }
    }

    public void follow() {
        Point2D direction = findTargetDirection();

        dimension.moveBy(direction.multiply(walkSpeed));
    }

    @Override
    public void attack() {
        Point2D direction = targetEntity.dimension.getPos().subtract(this.dimension.getPos());
        
        switch(livingType) {
            case BOMBER:
                new Projectile(ProjectileType.BOMB, dimension.getPos(), direction, 1);
                break;
            case SKELETON:
                new Projectile(ProjectileType.ARROW, dimension.getPos(), direction, 1);
                break;
            // more enemies
            
            case HERO:
                break;
        }
    }

    public void flee() {
        Point2D direction = findTargetDirection().multiply(-1);

        dimension.moveBy(direction.multiply(walkSpeed));
    }

    @Override
    public void getDamaged(double damage) {
        super.getDamaged(damage);
        if (0.15 < Math.random()*fear) {
            new Effect(EffectType.FEAR, (long)fear*100, this).startEffect();
        }
    }

    public Point2D findTargetDirection() { // direction from enemy to target
        Point2D targetPosition = targetEntity.dimension.getPos();
        return targetPosition.subtract(dimension.getPos()).normalize();
    }
    
    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }
}

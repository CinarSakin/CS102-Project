package com.game;

import com.game.Effect.EffectType;
import com.game.Projectile.ProjectileType;
import com.game.Projectile.TargetType;

import javafx.geometry.Point2D;

public class Enemy extends LivingEntity {
    private LivingEntity targetEntity;
    private boolean canAttack = true;

    public Enemy(LivingType lType, Point2D position, LivingEntity targetEntity, Room currentRoom, double diffMulti) {
        super(lType, position, currentRoom, diffMulti);
        this.targetEntity = targetEntity;
    }
    
    @Override
    public void update() {
        super.update();
        if (!inRange()) {
            super.follow(targetEntity);
        }
        else {
            if (canAttack) {
                attack();
                Effect.startEffect(new Effect(EffectType.TIRE, ((long)attackSpeed)*1, this)); // cooldown
            }
        }
    }

    @Override
    public void attack() {
        Point2D direction = findTargetDirection(targetEntity);
        
        switch(lType) {
            case BOMBER:
                new Projectile(ProjectileType.BOMB, TargetType.ALL, dimension.getPos(), direction, 1, currentArea);
                break;
            case SKELETON:
                new Projectile(ProjectileType.ARROW, TargetType.HERO, dimension.getPos(), direction, 1, currentArea);
                break;
            case WALKER:
                // melee animation and hit check
                break;
            // more enemies
            
            case HERO:
                break;
        }
    }

    public void flee() {
        Point2D direction = findTargetDirection(targetEntity).multiply(-1);

        move(direction.multiply(walkSpeed));
    }

    @Override
    public void getDamaged(double damage) {
        super.getDamaged(damage);
        if (0.15 < Math.random()*fear && inRange()) {
            Effect.startEffect(new Effect(EffectType.FEAR, (long)fear*100, this));
        }
    }
    
    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean inRange() {
        Point2D position = new Point2D(dimension.getX(), dimension.getY());
        return position.distance(targetEntity.dimension.getPos()) < range;
    }
}

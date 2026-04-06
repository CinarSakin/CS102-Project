package com.game;

import com.game.Effect.EffectType;
import com.game.Projectile.ProjectileType;
import com.game.Projectile.TargetType;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Enemy extends LivingEntity {
    private boolean canAttack = true;
    
    public Enemy(LivingType lType, Point2D position, Area currentArea, double diffMulti) {
        super(lType, position, currentArea, diffMulti);
    //    this.targetEntity = targetEntity;
        switch (lType) {
            case WALKER -> this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/entities/monster_idle.png"), Level.gridSize, 0, true, false);
            case BOMBER -> this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/entities/bomber.png"), Level.gridSize, 0, true, false);
            case SKELETON -> this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/entities/hero_idle_flipped.png"), Level.gridSize, 0, true, false);
        }

    }
    // @Override // if you can implement like this it will be easier.
    // public Enemy(int type, Point2D pos,LivingEntity tarEntity, double diffMulti){

    // }
    
    @Override
    public void update(double dt) {
        super.update(dt);
        
        if (!inAttackRange() && inRange()) {
            super.follow(Hero.getHero());
        }
        else {
            if (canAttack && inAttackRange()) {
                attack();
                new Effect(EffectType.TIRE, ((long)attackSpeed)*1, this).startEffect(); // cooldown
            }
        }
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
                new Projectile(ProjectileType.SLASH, TargetType.HERO, dimension.getPos(), direction, 1, currentArea);
                break;
            // more enemies
            
            case HERO:
                break;
        }
    }

    public void flee() {
        Point2D direction = findTargetDirection(Hero.getHero()).multiply(-1);

        move(direction.multiply(walkSpeed));
    }

    @Override
    public void getDamaged(double damage) {
        super.getDamaged(damage);
        if (0.15 < Math.random()*fear && inAttackRange()) {
            new Effect(EffectType.FEAR, (long)fear*100, this).startEffect();
        }
    }
    
    @Override
    public void reloadImages() {
        switch (lType) {
            case WALKER -> imageToDraw = new Image(getClass().getResourceAsStream("/sprites/entities/monster_idle.png"), Level.gridSize, 0, true, false);
            case BOMBER -> imageToDraw = new Image(getClass().getResourceAsStream("/sprites/entities/bomber.png"), Level.gridSize, 0, true, false);
            case SKELETON -> imageToDraw = new Image(getClass().getResourceAsStream("/sprites/entities/hero_idle_flipped.png"), Level.gridSize, 0, true, false);
            default -> {}
        }
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean inRange(){
        return (getDimension().distanceTo(Hero.getHero().getDimension().getPos())<range);
    }
    public boolean inAttackRange() {
        Point2D position = new Point2D(dimension.getX(), dimension.getY());
        return position.distance(Hero.getHero().dimension.getPos()) < attackRange;
    }
}

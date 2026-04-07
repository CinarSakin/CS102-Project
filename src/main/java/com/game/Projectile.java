package com.game;

import java.util.ArrayList;
import java.util.List;

import com.game.Effect.EffectType;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Projectile extends Entity {

    public final double EXPLODE_TIMER = 3;

    public enum ProjectileType {
        SLASH(
            new Image(Projectile.class.getResourceAsStream("/sprites/projectiles/slash1.png")),
            0, new Point2D(36, 48)
        ),
        BOMB(
            new Image(Projectile.class.getResourceAsStream("/sprites/projectiles/bomb.png")),
            3, new Point2D(48,48)
        ),  
        ARROW(
            new Image(Projectile.class.getResourceAsStream("/sprites/projectiles/arrow.png")),
            5, new Point2D(48, 24)
        ), 
        FLAMING_ARROW(
            new Image(Projectile.class.getResourceAsStream("/sprites/projectiles/flaming_arrow.png")),
            5, new Point2D(48, 24)
        ),
        BOSS_ORB(
            new Image(Projectile.class.getResourceAsStream("/sprites/projectiles/boss_orb.png")),
            2, new Point2D(64, 64)
        );

        private Image image;
        private double speed;
        private Point2D size;

        private ProjectileType(Image image, double speed, Point2D size) {
            this.image = image;
            this.speed = speed;
            this.size = size;
        }
    }
    
    private ProjectileType projType;
    private Point2D velocity;
    private double speed;
    private double lifeTime = 0;
    private TargetType targetType;

    private ArrayList<LivingEntity> hitEntities = new ArrayList<LivingEntity>();

    public enum TargetType {HERO, ENEMIES, ALL}

    public Projectile(ProjectileType projType, TargetType target, Point2D position, Point2D velocity, double speed, Area currentArea) {
        super(new Dimension(position.getX(), position.getY(), projType.size.getX(), projType.size.getY()), currentArea);
        this.projType = projType;
        this.velocity = velocity.normalize();
        this.speed = projType.speed * speed;
        this.targetType = target;
        this.imageToDraw = projType.image;

        if (projType == ProjectileType.SLASH) {
            if (Math.random() < .5) {
                imageToDraw = new Image(Projectile.class.getResourceAsStream("/sprites/projectiles/slash2.png"));
            }
        }
    }

    public void update(double dt) {
        
        dimension.moveBy(velocity.multiply(speed));
        
        Point2D center = dimension.getCenter();
        Area areaAtCenter = Dimension.findAreaAt(center);
    
        if (areaAtCenter == null) {
            despawn(); return;
        }
    
        if (Dimension.findAreaAt(getLeadingPoint()) == null){
            despawn(); return;
        }
    
        if (areaAtCenter != currentArea) {
            currentArea.unregister(this);
            currentArea = areaAtCenter;
            currentArea.register(this);
        }

        lifeTime += dt;
        if (lifeTime > 5) {
            despawn(); return;
        }

        if (projType.equals(ProjectileType.SLASH)){
            if (lifeTime > .15) {
                despawn(); return;
            }
            for (LivingEntity target : getTargets()){
                    if (!hitEntities.contains(target) && dimension.intersects(target.dimension)){
                        Hero h = Hero.getHero();
                        target.getDamaged(h.weapons[h.heldWeapon].damage);
                        hitEntities.add(target);
                    }                    
            }
        }

        else if (projType.equals(ProjectileType.BOMB)){
            speed = Math.max(speed*.95-.03, 0); // slows down
            if (lifeTime > EXPLODE_TIMER){
                //AnimationManager.updateImage(this);

                for (LivingEntity target : getTargets()){
                    double dist = target.getDimension().distanceTo(dimension);
                    if (dist < Level.gridSize*2){
                        target.getDamaged(Level.gridSize*15/(dist+Level.gridSize/2)); // damage range from 30 to 6
                        new Effect(EffectType.FEAR, 1000, target).startEffect();
                        new Effect(EffectType.BURN, 1000, target).startEffect();
                    }                    
                }
            }
        }

        else if (projType.equals(ProjectileType.BOSS_ORB)) {
            speed = speed*1.2;
        }

        else { // arrow
            for (LivingEntity target : getTargets()){
                if (dimension.intersects(target.dimension)) {
                    target.getDamaged(10);
                    this.despawn();
                }
            }
        }
    }

    public ArrayList<? extends LivingEntity> getTargets() {
        return switch (this.targetType) {
            case HERO -> new ArrayList<>(List.of(Hero.getHero()));
            case ENEMIES -> new ArrayList<>(currentArea.getEnemies());
            case ALL -> new ArrayList<>(currentArea.getLivingEntities());
            default -> new ArrayList<>();
        };
    }

    private Point2D getLeadingPoint() {
        double lx = (velocity.getX() >= 0) ? dimension.getRightX() : dimension.getX();
        double ly = (velocity.getY() >= 0) ? dimension.getBottomY() : dimension.getY();
        return new Point2D(lx, ly);
    }

    public double getLifeTime() {
        return lifeTime;
    }

    public ProjectileType getType() {
        return projType;
    }

    @Override
    public void reloadImages() {
        if (projType != null) imageToDraw = projType.image;
    }

}

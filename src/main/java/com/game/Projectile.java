package com.game;

import java.util.ArrayList;
import java.util.List;

import com.game.Effect.EffectType;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Projectile extends Entity {

    public enum ProjectileType {
        MELEE(
            new Image("slash.png"),
            3, new Point2D(8, 8)
        ),
        BOMB(
            new Image("bomb.png"),
            3, new Point2D(16, 16)
        ),  
        ARROW(
            new Image("arrow.png"),
            5, new Point2D(12, 12)
        ), 
        FLAMING_ARROW(
            new Image("flaming_armor.png"),
            5, new Point2D(12, 12)
        ),
        BOSS_ORB(
            new Image("flaming_armor.png"),
            2, new Point2D(20, 20)
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

    public enum TargetType {HERO, ENEMIES, ALL}

    public Projectile(ProjectileType projType, TargetType target, Point2D position, Point2D velocity, double speed, Area currentArea) {
        super(new Dimension(position.getX(), position.getY(), projType.size.getX(), projType.size.getY()), (Room)(currentArea));
        this.projType = projType;
        this.velocity = velocity.normalize();
        this.speed = projType.speed * speed;
        this.targetType = target;

        this.animManager = new AnimationManager(projType);
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

        if (projType.equals(ProjectileType.BOMB)){
            speed = Math.max(speed*.95-.03, 0); // slows down
            if (lifeTime > 3){
                animManager.playAnim(ProjectileType.BOMB);

                for (LivingEntity target : getTargets()){
                    double dist = target.getDimension().distanceTo(dimension);
                    if (dist < 20){
                        target.getDamaged(600/(dist+10)); // damage range from 20 to 60
                    }                    
                }
            }
        }
        else if (projType.equals(ProjectileType.BOSS_ORB)) {
            
        }
        else{ // arrow
            for (LivingEntity target : getTargets()){
                if (dimension.intersects(target.dimension)) {

                }
            }
        }
    }

    public ArrayList<? extends LivingEntity> getTargets() {
        return switch (this.targetType) {
            case HERO -> new ArrayList<>(List.of(Hero.getHero()));
            case ENEMIES -> currentArea.getEnemies();
            case ALL -> currentArea.getLivingEntities();
            default -> new ArrayList<>();
        };
    }

    private Point2D getLeadingPoint() {
        double lx = (velocity.getX() >= 0) ? dimension.getRightX() : dimension.getX();
        double ly = (velocity.getY() >= 0) ? dimension.getBottomY() : dimension.getY();
        return new Point2D(lx, ly);
    }

    @Override
    public void draw() {} // todo
}

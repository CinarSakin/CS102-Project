package com.game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Projectile extends Entity {

    enum ProjectileType {
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
    private int tick = 0;

    public Projectile(ProjectileType projType, Point2D position, Point2D velocity, double speed, Room currentRoom) {
        super(new Dimension(position.getX(), position.getY(), projType.size.getX(), projType.size.getY()), currentRoom);
        this.projType = projType;
        this.velocity = velocity.normalize();
        this.speed = projType.speed * speed;
    }

    public void update() {
        
        dimension.moveBy(velocity.multiply(speed));
        // will add check for walls
        
        if (projType.equals(ProjectileType.BOMB)){
            speed = Math.max(speed*.95-.03, 0); // slows down
            tick++;
            if (tick > 120){
                // ToDo: explode effect
                for (LivingEntity living : LivingEntityManager.getLivingEntities()){
                    double dist = living.getDimension().distanceTo(dimension);
                    if (dist < 14){
                        living.getDamaged(300/(dist+6)); // damage range from 50 to 15
                    }                    
                }
            }
        }
        else if (projType.equals(ProjectileType.BOSS_ORB)) {
            
        }
        else{ // arrow
            // hits enemies if shooted by the hero
            // hits only hero if shooted by an enemy
        }
    }

    @Override
    public void draw() {} // todo
}

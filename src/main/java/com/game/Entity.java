package com.game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public abstract class Entity {
    protected Room currentRoom;
    protected Dimension dimension;
    protected Image entityImage;

    public Entity(Dimension dimension, Room currentRoom) {
        if (dimension == null) {
            throw new IllegalArgumentException("Dimension cannot be null.");
        }
        this.dimension = dimension;
        this.currentRoom = currentRoom;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        if (dimension == null) {
            throw new IllegalArgumentException("Dimension cannot be null.");
        }
        this.dimension = dimension;
    }

    // COLLISION
    public boolean intersects(Entity other) {
        return other != null &&
               this.dimension.intersects(other.dimension);
    }
    
    public Point2D findTargetDirection(Entity targetEntity) { // direction from this to targetEntity
        Point2D targetPosition = targetEntity.dimension.getPos();
        return targetPosition.subtract( this.dimension.getPos()).normalize();
    }

    public void despawn() {
        if (currentRoom != null && currentRoom.entitys != null) {
            currentRoom.entitys.remove(this);
        }
        if (this instanceof LivingEntity) {
            LivingEntityManager.unregister((LivingEntity) this);
        }
    }
    public abstract void update();

    public abstract void draw();

    public Image getImage(){return this.entityImage;}
    public abstract void setImage();
}
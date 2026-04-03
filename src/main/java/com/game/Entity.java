package com.game;

import javafx.geometry.Point2D;

public abstract class Entity {
    protected Room currentRoom;
    protected Dimension dimension;

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

    // MOVEMENT
    public void move(double dx, double dy) {
        if (dimension.insideOf(currentRoom.getDimension())) {
            dimension.moveBy(dx, dy);
        }
    }

    public void move(Point2D velocity) {
        if (dimension.insideOf(currentRoom.getDimension())) {
            dimension.moveBy(velocity);
        }
    }

    public Point2D findTargetDirection(Entity targetEntity) { // direction from this to targetEntity
        Point2D targetPosition = targetEntity.dimension.getPos();
        return targetPosition.subtract( this.dimension.getPos()).normalize();
    }

    // COLLISION
    public boolean intersects(Entity other) {
        return other != null &&
               this.dimension.intersects(other.dimension);
    }

    public void despawn() {
        currentRoom.getEntities().remove(this); // done
    }

    public abstract void update();

    public abstract void draw();
}
package com.game;

import javafx.geometry.Point2D;

public abstract class Entity {

    protected Dimension dimension;

    public Entity(Dimension dimension) {
        if (dimension == null) {
            throw new IllegalArgumentException("Dimension cannot be null.");
        }
        this.dimension = dimension;
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
        // TODO: remove from game.
    }

    // updates and draws
    public abstract void update();
    
}
package com.game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public abstract class Entity {
    public transient Area currentArea;
    protected Dimension dimension;
    protected AnimationManager animManager;
    public transient Image imageToDraw;

    public Entity(Dimension dimension, Area currentArea) {
        if (dimension == null) {
            throw new IllegalArgumentException("Dimension cannot be null.");
        }
        this.dimension = dimension;
        this.currentArea = currentArea;

        currentArea.register(this);
    }

    public Dimension getDimension() {
        return dimension;
    }

    public Image getImage() {
        return imageToDraw;
    }

    public void setDimension(Dimension dimension) {
        if (dimension == null) {
            throw new IllegalArgumentException("Dimension cannot be null.");
        }
        this.dimension = dimension;
    }

    // MOVEMENT
    public void move(double dx, double dy) {
        if (dimension.insideOf(currentArea.getDimension())) {
            dimension.moveBy(dx, dy);
        }
    }

    public void move(Point2D velocity) {
        if (dimension.insideOf(currentArea.getDimension())) {
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
        currentArea.unregister(this);
    }

    public abstract void update(double dt);

}
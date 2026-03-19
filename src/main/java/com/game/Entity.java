package com.game;

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

    // COLLISION
    public boolean intersects(Entity other) {
        return other != null &&
               this.dimension.intersects(other.dimension);
    }

    public void despawn() {
        // TODO: remove from game.
    }

    public abstract void update();

    public abstract void draw();
}
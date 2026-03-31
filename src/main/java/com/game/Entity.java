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

    // updates and draws
    public abstract void update();
    private void draw(){}//make a predefined drawer for all entities but you can change the sprite and size values from other classes.

}
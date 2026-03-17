package com.game;

public abstract class Entity {
    private Dimension dimension;
    private boolean active;

    public Entity(Dimension dimension) {
        if (dimension == null) {
            throw new IllegalArgumentException("Dimension cannot be null.");
        }
        this.dimension = dimension;
        this.active = true;
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

    public double getX() {
        return dimension.getX();
    }

    public double getY() {
        return dimension.getY();
    }

    public double getWidth() {
        return dimension.getWidth();
    }

    public double getHeight() {
        return dimension.getHeight();
    }

    public void setPosition(double x, double y) {
        dimension.setPosition(x, y);
    }

    public void move(double dx, double dy) {
        dimension.move(dx, dy);
    }

    public boolean intersects(Entity other) {
        return other != null && this.dimension.intersects(other.dimension);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    public abstract void update();
}
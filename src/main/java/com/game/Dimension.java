package com.game;

public class Dimension {
    private double x;
    private double y;
    private double width;
    private double height;

    public Dimension(double x, double y, double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height cannot be negative.");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        if (width < 0) {
            throw new IllegalArgumentException("Width cannot be negative.");
        }
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (height < 0) {
            throw new IllegalArgumentException("Height cannot be negative.");
        }
        this.height = height;
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean intersects(Dimension other) {
        boolean noCollision =
    this.x + this.width <= other.x ||   // completely left
    this.x >= other.x + other.width ||  // completely right
    this.y + this.height <= other.y ||  // completely above
    this.y >= other.y + other.height;   // completely below

return !noCollision;
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
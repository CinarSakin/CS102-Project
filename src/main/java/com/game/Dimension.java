package com.game;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;

public class Dimension {

    // variables
    private double x;
    private double y;
    private double width;
    private double height;
    private Area ownerArea;

    // constructor
    public Dimension(double x, double y, double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height cannot be negative.");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // getters
    public double getX() {return x;}
    public double getY() {return y;}
    public double getRightX() {return x + getWidth();}
    public double getBottomY() {return y + getHeight();}

    public Point2D getPos() {return new Point2D(x, y);}

    public double getWidth() {return width;}

    public double getHeight() {return height;}

    public void setX(double a){x=a;}
    public void setY(double a){y=a;}
    public void setHeight(double a){height=a;}
    public void setWidth(double a){width=a;}

    public Point2D getCenter() {
        return new Point2D(x + width / 2, y + height / 2);
    }

    public BoundingBox getBoundingBox() {
        return new BoundingBox(x, y, width, height);
    }

    // will return Room or Hall object.
    public Area getArea() {return ownerArea;}


    public double distanceTo(Dimension other) {
        if (other == null) return 0;
        return this.getCenter().distance(other.getCenter());
    }

    public double distanceTo(Point2D point) {
        if (point == null) return 0;
        return this.getCenter().distance(point);
    }

    // setters

    /** @return {@code true} if a collision occurred with the area bounds, {@code false} otherwise. */
    public boolean moveByWithCollision(Area area, double dx, double dy) {
        Dimension areaDimension = area.getDimension();
        
        double originalNextX = getX() + dx;
        double originalNextY = getY() + dy;
        double nextX = originalNextX;
        double nextY = originalNextY;
    
        if (nextX < areaDimension.getX()) {nextX = areaDimension.getX();} 
        else if (nextX + getWidth() > areaDimension.getRightX()) {
            nextX = areaDimension.getRightX() - getWidth();
        }

        if (nextY < areaDimension.getY()) {nextY = areaDimension.getY();} 
        else if (nextY + getHeight() > areaDimension.getBottomY()) {
            nextY = areaDimension.getBottomY() - getHeight();
        }
    
        setX(nextX);
        setY(nextY);

        return (nextX != originalNextX) || (nextY != originalNextY);
    }

    /** @return {@code true} if a collision occurred with the area bounds, {@code false} otherwise. */
    public boolean moveByWithCollision(Area area, Point2D velocity) {
        return moveByWithCollision(area, velocity.getX(), velocity.getY());
    }

    public void moveBy(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void moveBy(Point2D velocity) {
        moveBy(velocity.getX(), velocity.getY());
    }

    public void moveTo(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void moveCenterTo(double centerX, double centerY) {
        this.x = centerX - width / 2;
        this.y = centerY - height / 2;
    }

    public void moveCenterTo(Point2D position) {
        this.x = position.getX() - width / 2;
        this.y = position.getY() - height / 2;
    }

    public Object setArea(Area area) {
        return this.ownerArea = area;
    }
    
    public void setSize(double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width/Height can not be negative.");
        }
        this.width = width;
        this.height = height;
    }

    // methods
    public static Area findAreaAt(Point2D point) {
        for (Area area : Level.getLevel().getAreas()) {
            if (area.getDimension().contains(point)) {
                return area;
            }
        }
        return null;
    }

    public boolean contains(Point2D point) {
        return point.getX() >= this.getX() && 
               point.getX() <= (this.getRightX()) &&
               point.getY() >= this.getY() && 
               point.getY() <= (this.getBottomY());
    }

    public boolean intersects(Dimension other) {
        return this.x < other.x + other.width &&
            this.x + this.width > other.x &&
            this.y < other.y + other.height &&
            this.y + this.height > other.y;
    }

    public boolean insideOf(Dimension other){
        return other.getX() < getX() && getRightX() < other.getRightX() &&
        other.getY() < getY() && getBottomY() < other.getBottomY();
    }

    public boolean insideOf(Area area){
        return insideOf(area.getDimension());
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
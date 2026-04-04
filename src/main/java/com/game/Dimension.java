package com.game;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;

public class Dimension {

    // variables
    private double x;
    private double y;
    private double width;
    private double height;


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

    public double distanceTo(Dimension other) {
        if (other == null) return 0;
        return this.getCenter().distance(other.getCenter());
    }

    public double distanceTo(Point2D point) {
        if (point == null) return 0;
        return this.getCenter().distance(point);
    }

    // setters
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
    
    public void setSize(double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width/Height can not be negative.");
        }
        this.width = width;
        this.height = height;
    }

    // methods
    public boolean intersects(Dimension other) {
        return this.x < other.x + other.width &&
            this.x + this.width > other.x &&
            this.y < other.y + other.height &&
            this.y + this.height > other.y;
    }

    public boolean insideOf(Dimension other){
        if(other.getX()< this.getX() && this.getX()+this.getWidth()<other.getX()+other.getWidth()
        && other.getY()<this.getY() && this.getY()+this.getHeight()<other.getY()+other.getHeight())return true;
        else return false;
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
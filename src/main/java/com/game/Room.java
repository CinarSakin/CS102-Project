package com.game;

import java.util.ArrayList;

import javafx.geometry.Point2D;

public class Room {
    //class variables
    public static final int MINIMUM_SIZE = 80;//1 tile is 4 units for measurement


    //Instance variables
    
    public double dif;
    private final int MAXIMUM_ENEMIES = (int)(10 * dif);
    public ArrayList<Entity> entitys;
    public Dimension dim;
    public Room right;
    public Room left;
    public RoomType type ;
    public ArrayList<Room> hNeighbors;
    public ArrayList<Room> vNeighbors;
    public ArrayList<Hall> hHalls;
    public ArrayList<Hall> vHalls;
    
    

    public Room(double x1, double y1, double x2, double y2){
        this(x1, y1, x2, y2, (int)(Math.random()*4));
    }
    public Room(double x1, double y1, double x2, double y2, int newType){
        if(newType == 0)type = RoomType.TYPE_GATE;
        if(newType == 1)type = RoomType.TYPE_NORMAL;
        if(newType == 2)type = RoomType.TYPE_LOOT;
        if(newType == 1)type = RoomType.TYPE_NORMAL;
        if(newType == 1)type = RoomType.TYPE_NORMAL;
        dim = new Dimension(x1, y1, x2-x1, y2-y1);
        hNeighbors = new ArrayList<Room>();
        vNeighbors = new ArrayList<Room>();
        hHalls = new ArrayList<Hall>();
        vHalls = new ArrayList<Hall>();
        dif = 1+ Level.levelNo/10;
    }

    public boolean divide(double minSize){
        if (left != null) { // If not a leaf, recurse
        if (Math.random() < 0.5) return left.divide(minSize);
        else return right.divide(minSize);
        }

        double w = dim.getWidth();
        double h = dim.getHeight();
        double x1 = dim.getX();
        double y1 = dim.getY();
        double x2 = w + x1;
        double y2 = h + y1;

        if (w < minSize && h < minSize) return false;

        if (w > h) { // Split vertically
            double mid = x1 + w * random(0.3, 0.7);
            left = new Room(x1, y1, mid, y2);
            right = new Room(mid, y1, x2, y2);
        } else { // Split horizontally
            double mid = y1 + h * random(0.3, 0.7);
            left = new Room(x1, y1, x2, mid);
            right = new Room(x1, mid, x2, y2);
        }

        return true;
    }

    public void shrink(double minSize) {
        if (left != null) {
        left.shrink(minSize);
        right.shrink(minSize);
        } else {
            double x1 = dim.getX();
            double y1 = dim.getY();
            double x2 = dim.getWidth() + dim.getX();
            double y2 = dim.getHeight() + dim.getY();
            double w = x2 - x1;
            double h = y2 - y1;
            double nw = Math.max(minSize, (w * random(0.4, 0.9)));
            double nh = Math.max(minSize, (h * random(0.4, 0.9)));
            dim.setX(x1 + (w - nw) / 2);
            dim.setWidth((x2 - (w - nw) / 2)-x1);
            dim.setY(y1 + (h - nh) / 2);
            dim.setHeight((y2 - (h - nh) / 2)-y1);
        }
    }

    private void spawnEntities(int type){
        //ToDo
        if(type == 0) return;

        double ENEMY_COUNT = min((int)(Math.random()*MAXIMUM_ENEMIES),5);
        for(int i = 0; i < ENEMY_COUNT;i++){
            entitys.add(new Enemy(null, randomPos(), this.Hero, MAXIMUM_ENEMIES));
        }

    }


    //UPDATERS
    public void draw(){
        if (left != null) {
        left.draw();
        right.draw();
        } else {
            for (Hall h : hHalls) h.draw();
            for (Hall h : vHalls) h.draw();
        }
    }

    //EXTRA METHODS
    private static Point2D randomPos(){
        return null;
        //TODO: make a random dimension, convertes it to a point inside the room for enemy to spawn in that position
    }
    private static double random(double a, double b){
        return Math.random()*(b-a)+a;
    }

    private static double max(double x, double a){
        if(a<x){return x;}
        return a;
    }

    private static double min(double x, double a){
        if(x>a){return x;}
        return a;
    }

    //GETTER SETTERS
    public double getX1(){return dim.getX();}
    public double getY1(){return dim.getY();}
    public double getX2(){return dim.getWidth() + dim.getX();}
    public double getY2(){return getHeight() + dim.getY();}
    public double getHeight(){return dim.getHeight();}
    public double getWidth(){return dim.getWidth();}
    public RoomType getType(){return this.type;}
    public Dimension getDimension(){return this.dim;}

    //enumeration
    enum RoomType{
        TYPE_GATE{
            private void spawnEntities(int i){
                spawnEntities(0);
            }
        },
        TYPE_NORMAL{
            private void spawnEntities(int i){
                spawnEntities(1);
            }
        },
        TYPE_LOOT{
            private void spawnEntities(int i){
                spawnEntities(2);
            }
        },
        TYPE_PUZZLE{
            private void spawnEntities(int i){
                spawnEntities(3);
            }
        },
        TYPE_BOSS{
            private void spawnEntities(int i){
                spawnEntities(4);
            }
        } 
    }

}

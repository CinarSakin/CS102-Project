package com.game;

import java.util.ArrayList;

import javafx.geometry.Point2D;

public class Room extends Area {
    //class variables
    public static final int MINIMUM_SIZE = 80;//1 tile is 4 units for measurement
    public static ArrayList<Hall> hHalls;
    public static ArrayList<Hall> vHalls;

    //Instance variables
    
    public double dif;
    private final int MAXIMUM_ENEMIES = (int)(10 * dif);
    public Dimension dim;
    public Room right;
    public Room left;
    public RoomType type ;
    public ArrayList<Room> hNeighbors;
    public ArrayList<Room> vNeighbors;
    
    
    
    

    public Room(double x1, double y1, double x2, double y2){
        this(x1, y1, x2, y2, (int)(Math.random()*4));
    }
    public Room(double x1, double y1, double x2, double y2, int newType){
        super(new Dimension(x1, y1, x2-x1, y2-y1));
        if(newType == 0)type = RoomType.PORTAL;
        if(newType == 1)type = RoomType.NORMAL;
        if(newType == 2)type = RoomType.LOOT;
        if(newType == 1)type = RoomType.NORMAL;
        if(newType == 1)type = RoomType.NORMAL;
        hNeighbors = new ArrayList<Room>();
        vNeighbors = new ArrayList<Room>();
        hHalls = new ArrayList<Hall>();
        vHalls = new ArrayList<Hall>();
        dif = 1+ Level.levelNo/10;
        spawnEntities();
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

    private void spawnEntities(){
        //ToDo
        switch (this.type) {
            case PORTAL:
                break;
            case NORMAL:
                double enemyCount = max(5,(int)(Math.random()*MAXIMUM_ENEMIES));
                for(int i = 0; i < enemyCount; i++ ){
                    new Enemy(LivingEntity.RandomType(), randomPos(), Hero.getHero(), this, dif);
                    //add enemies in random points inside of the room
                    //If needed an enemys array can be added for less confision while updateing
                }
                break;
            case LOOT:
                int lootCount = (int)(Math.random()*3);
                for(int i = 0; i < lootCount; i++){
                    new WorldEntity();// loots will be added inside a predefined(?) places in the room
                }
                break;
            case PUZZLE:
                //EREN KOZAN TODO:MAKE A PUZZLE GENERATION
                
                break;
            case BOSS:
                //Create a boss in the middle of the room which can or cannot move according to the move patern of its
                
                break;
                
            default:
                throw new AssertionError();
        }
        

    }


    //UPDATERS

    /* şuanlık Area classına update ve draw koydum
    public void update(){}

    public void draw(){
        if (left != null) {
        left.draw();
        right.draw();
        } else {
            for (Hall h : hHalls) h.draw();
            for (Hall h : vHalls) h.draw();
            //make a drawing principle for entities and room itself.(first room then entities.)
            //roomdrawing
            for(Entity e: entities) e.update();
        }
    } */

    //EXTRA METHODS
    private Point2D randomPos(){
        int x = (int)(this.dim.getX() + Math.random()*dim.getWidth()+1);
        int y = (int)(this.dim.getY() + Math.random()*dim.getHeight()+1);
        return new Point2D(x, y);
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
    public RoomType getType(){return this.type;}
    public static ArrayList<Hall> getHHalls(){return hHalls;}
    public static ArrayList<Hall> getVHalls(){return vHalls;}


    //enumeration
    enum RoomType{
        PORTAL{
            protected void spawnEntities(int i){
                spawnEntities(0);
            }
        },
        NORMAL{
            protected void spawnEntities(int i){
                spawnEntities(1);
            }
        },
        LOOT{
            protected  void spawnEntities(int i){
                spawnEntities(2);
            }
        },
        PUZZLE{
            protected void spawnEntities(int i){
                spawnEntities(3);
            }
        },
        BOSS{
            protected void spawnEntities(int i){
                spawnEntities(4);
            }
        } 
    }

}

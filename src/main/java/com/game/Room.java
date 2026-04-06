package com.game;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Room extends Area {
    //class variables
    public static int minSize = Level.gridSize*3;
    public static ArrayList<Hall> hHalls = new ArrayList<Hall>();
    public static ArrayList<Hall> vHalls = new ArrayList<Hall>();


    //Instance variables
    public double dif;
    private final int MAXIMUM_ENEMIES = (int)(10 * dif);
    public Room right;
    public Room left;
    public RoomType type ;
    public ArrayList<Room> hNeighbors;
    public ArrayList<Room> vNeighbors;    
    

    public Room(double x1, double y1, double width, double height){
        this(x1, y1, width, height, (int)(Math.random()*3+1));
    }
    public Room(double x1, double y1, double width, double height, int newType){
        super(new Dimension(x1, y1, width, height));
        if(newType == 0)type = RoomType.PORTAL;
        if(newType == 1)type = RoomType.NORMAL;
        if(newType == 2)type = RoomType.LOOT;
        if(newType == 3)type = RoomType.NORMAL;
        if(newType == 4)type = RoomType.NORMAL;
        hNeighbors = new ArrayList<Room>();
        vNeighbors = new ArrayList<Room>();
        dif = 1+ Level.levelNo/10;

    }

    public boolean divide(double minSize){
        if (left != null) { // If not a leaf, recurse
            // if (Math.random() < 0.5) return left.divide(minSize);
            // else return right.divide(minSize);
            boolean divided = false;
            if (Math.random() < 0.5) {
                divided = left.divide(minSize);
                if (!divided) divided = right.divide(minSize);
            } else {
                divided = right.divide(minSize);
                if (!divided) divided = left.divide(minSize);
            }
            return divided;
        }

        double w = dim.getWidth();
        double h = dim.getHeight();
        double x1 = dim.getX();
        double y1 = dim.getY();

        if (w < minSize && h < minSize) return false;

        if (w > h) { // Split vertically
            double mid = snapToGrid(w * random(0.3, 0.7));
            left = new Room(x1, y1, mid, h);
            right = new Room(x1 + mid, y1, w - mid, h);
        } else { // Split horizontally
            double mid = snapToGrid(h * random(0.3, 0.7));
            left = new Room(x1, y1, w, mid);
            right = new Room(x1, y1 + mid, w, h - mid);
        }

        return true;
    }

    public void shrink() {
        if (left != null) {
            left.shrink();
            right.shrink();
        } else {
            double nw = snapToGrid(Math.max(minSize, (dim.getWidth() * random(0.4, 0.9))));
            double nh = snapToGrid(Math.max(minSize, (dim.getHeight() * random(0.4, 0.9))));

            double offsetX = snapToGrid((dim.getWidth() - nw) / 2);
            double offsetY = snapToGrid((dim.getHeight() - nh) / 2);

            dim.setX(dim.getX() + offsetX);
            dim.setY(dim.getY() + offsetY);
            dim.setWidth(nw);
            dim.setHeight(nh);
        }
    }

    private double snapToGrid(double value) {
        return Math.round(value/Level.gridSize) * Level.gridSize;
    }

    public void spawnEntities(){
        //ToDo
        switch (this.type) {
            case PORTAL:
                break;
            case NORMAL:
                double enemyCount = max(5,(int)(Math.random()*MAXIMUM_ENEMIES));
                for(int i = 0; i < enemyCount; i++ ){
                    new Enemy(LivingEntity.RandomType(), randomPos(), this, dif);
                    //add enemies in random points inside of the room
                    //If needed an enemys array can be added for less confision while updateing
                }
                break;
            case LOOT:
                int lootCount = (int)(Math.random()*3);
                for(int i = 0; i < lootCount; i++){
//                    new WorldEntity();// loots will be added inside a predefined(?) places in the room
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
        int x = (int)(dim.getX() + Math.random()*dim.getWidth()+1);
        int y = (int)(dim.getY() + Math.random()*dim.getHeight()+1);
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
    public void setStartingRoom(){this.type = RoomType.PORTAL;}


    //enumeration
    public enum RoomType{
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

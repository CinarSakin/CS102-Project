package com.game;

import java.util.ArrayList;

public class Level {

    // class variables
    public static double gridSize = 20;
    private static Level currentLevel;
    public static int levelNo;
    public static Hero hero;
    public static boolean heroSetIn = false;
    
    // instance variables
    private ArrayList<Room> rooms;
    public ArrayList<Area> areas;
    private ArrayList<Entity> entities;
    private Room root;
    private int numRooms;
    
    private Level(int levelCount){
        this.levelNo = levelCount;
        this.numRooms = 5 + levelNo;
        generateLevel();
        hero.getHero().getDimension().setPosition(getStartingRoom().getDimension().getCenter());

    }

    // SAVEING MECHANISMS
    public static Level constructFromSave(char saveSlot) throws Exception {
        if (getLevel() != null) throw new IllegalStateException("There is already a Level instance!");

        Level loadedLevel = SaveManager.loadLevel(saveSlot);
    
        if (loadedLevel == null) {
            throw new Exception("Level data could not be loaded!");
        }

        for (Area area : loadedLevel.areas) {
            area.livingEntities = new ArrayList<>();
            area.enemies = new ArrayList<>();
    
            for (Entity e : area.getEntities()) {
                e.currentArea = area; 
    
                if (e instanceof LivingEntity) area.livingEntities.add((LivingEntity) e);
                if (e instanceof Enemy) area.enemies.add((Enemy) e);
                
                // e.reloadSprites(); 
            }
        }
    
        return currentLevel = loadedLevel;
    }

    public static Level constructNew(int levelCount) {
        if (getLevel() != null) throw new IllegalStateException("There is already a Level instance!");
        currentLevel = new Level(levelCount);
        return currentLevel;
    }

    public static void save(){
        try (java.io.PrintWriter out = new java.io.PrintWriter("save" + levelNo + ".txt")) {
            out.println(levelNo);
            out.println(Hero.getHero().health);  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    //CONSTRUCTING METHODS
    private void generateLevel(){
        root = new Room(0, 0, 500, 500,0);
        rooms = new ArrayList<>();
        rooms.add(root);
        
        
        divide(); 
        
        for (Room r : rooms) {
            if (r.type == Room.RoomType.PORTAL) {
                if(heroSetIn)break;
                Dimension newDim = new Dimension((r.getX1()+r.getWidth())/2-hero.getDimension().getWidth()/2,
                    (r.getY1()+r.getHeight())/2 - hero.getDimension().getHeight(),
                    hero.getDimension().getWidth(),hero.getDimension().getHeight());
                hero.setDimension(newDim);
                
            }
        }
    }

    private void divide() {
        int count = 1;
        while (count < numRooms) {
        if (root.divide(gridSize * 2)) count++;
        }
    }

    private void getLeaves(Room c) {
        if (c.left == null) rooms.add(c);
        else {
        getLeaves(c.left);
        getLeaves(c.right);
        }
    }

    private void findNeighbors() {
        getLeaves(root);
        for (Room c : rooms) {
            for (Room o : rooms) {
                if (c == o) continue;
                if (c.getX2() == o.getX1() && max(c.getY1(), o.getY1()) < min(c.getY2(), o.getY2())) c.hNeighbors.add(o);
                if (c.getY2() == o.getY1() && max(c.getX1(), o.getX1()) < min(c.getX2(), o.getX2())) c.vNeighbors.add(o);
            }
        }
    }

    private void addHalls() {
        for (Room c : rooms) {
            for (Room n : c.hNeighbors) {
                double overlapTop = max(c.getY1(), n.getY1());
                double overlapBot = min(c.getY2(), n.getY2());
                if (overlapBot - overlapTop > gridSize) {
                double y = random(overlapTop, overlapBot - gridSize);
                c.hHalls.add(new Hall(c.getX2(), y, n.getX1(), y + gridSize));
                }
            }

            // Repeat similar logic for vNeighbors/vHalls
            for (Room n : c.vNeighbors) {
                double overlapTop = max(c.getY1(), n.getY1());
                double overlapBot = min(c.getY2(), n.getY2());
                if (overlapBot - overlapTop > gridSize) {
                    double y = random(overlapTop, overlapBot - gridSize);
                    c.vHalls.add(new Hall(c.getX2(), y, n.getX1(), y + gridSize));
                }
            }
        }
    }
    public void shrink() { root.shrink(gridSize * 2); }
    
    //UPDATEING METHODS
    public void update(double dt){
        for (Area a : getAreas()){
            a.update(dt);
        }
    }    
    
    //EXTRA METHODS
    private double random(double a, double b){
        return Math.random()*(b-a)+a;
    }

    private double max(double x, double a){
        if(a<x){return x;}
        return a;
    }

    private double min(double x, double a){
        if(x>a){return x;}
        return a;
    }

    //GETTER,SETTERS
    public static Level getLevel() {
        return currentLevel;
    }
    public static ArrayList<Room> getRooms(){
        return Level.getLevel().rooms;
    }
    public static ArrayList<Area> getAreas(){
        return Level.getLevel().areas;
    }
    public static void setHero(Hero aHero){hero = aHero;}
    

}
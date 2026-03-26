package com.game;

import java.util.ArrayList;

public class Level {

    // class variables
    public static double gridSize = 20;
    private static Level currentLevel;

    // instance variables
    public int levelNo;
    private ArrayList<Room> rooms;
    private ArrayList<Entity> entities;
    private Room root;
    private int numRooms;
    
    private Level(int levelCount){
        this.levelNo = levelCount;
        this.numRooms = 5 + levelNo;
        generateLevel();
    }

    private void generateLevel(){

    }

    private Level(char saveSlot){
        //read from the save file
    }

    public static Level constructFromSave(char saveSlot) {
        if (getLevel() != null) throw new IllegalStateException("There is already a Level instance!");
        currentLevel = new Level(saveSlot);
        return currentLevel;
    }

    public static Level constructNew(int levelCount) {
        if (getLevel() != null) throw new IllegalStateException("There is already a Level instance!");
        currentLevel = new Level(levelCount);
        return currentLevel;
    }

    public static Level getLevel() {
        return currentLevel;
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

    public void save(){
        //TODO
    }

    public void shrink() { root.shrink(gridSize * 2); }
    public void draw() { root.draw(); }

    public boolean isInBounds(Entity a){
        for(int i = 0 ; i< rooms.size() ;i++){
            Room b = rooms.get(i);
            Dimension roomDim = b.getDimension();
            if(){return true;}
        }
        return false;
    }

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

}
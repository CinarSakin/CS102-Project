package com.game;

import java.util.ArrayList;

public class Level {
    //class variables
    public static double gridSize = 20;

    //instance variables
    public int levelNo;
    private ArrayList<Room> rooms;
    private ArrayList<Entity> entitys;
    private Room root;
    private int numRooms;
    
    public Level(int levelCount){
        this.levelNo = levelCount;
        this.numRooms = 5 + levelNo;
        divide();
    }

    public Level(char saveSlot){
        //read from a file
    }
    void divide() {
        int count = 1;
        while (count < numRooms) {
        if (root.divide(gridSize * 2)) count++;
        }
    }

    void getLeaves(Room c) {
        if (c.left == null) rooms.add(c);
        else {
        getLeaves(c.left);
        getLeaves(c.right);
        }
    }

    void findNeighbors() {
        getLeaves(root);
        for (Room c : rooms) {
        for (Room o : rooms) {
            if (c == o) continue;
            if (c.x2 == o.x1 && max(c.y1, o.y1) < min(c.y2, o.y2)) c.hNeighbors.add(o);
            if (c.y2 == o.y1 && max(c.x1, o.x1) < min(c.x2, o.x2)) c.vNeighbors.add(o);
        }
        }
    }

    void addHalls() {
        for (Room c : rooms) {
        for (Room n : c.hNeighbors) {
            double overlapTop = max(c.y1, n.y1);
            double overlapBot = min(c.y2, n.y2);
            if (overlapBot - overlapTop > gridSize) {
            float y = random(overlapTop, overlapBot - gridSize);
            c.hHalls.add(new Hall(c.dim.getWidth(), y, n.dim.getX(), y + gridSize));
            }
        }
        // Repeat similar logic for vNeighbors/vHalls
        }
    }

    public void save(){
        //TODO
    }

    void shrink() { root.shrink(gridSize * 2); }
    void display() { root.display(); }

    private double random(double a, double b){
        return Math.random()*(b-a)+a;
    }

    private double max(double x, double a){
        if(a<x){return x;}
        return a;
    }

}
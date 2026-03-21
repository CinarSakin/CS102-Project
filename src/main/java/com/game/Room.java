package com.game;

import java.util.ArrayList;

public class Room {
    //class variables
    public static final int MINIMUM_SIZE = 80;//1 tile is 4 units for measurement


    //Instance variables
    ArrayList<Entity> entitys;
    public Dimension dim;
    public Room right;
    public Room left;
    public int type = (int)(Math.random()*2);
    public Room[] hNeighbors;
    public Room[] vNeighbors;
    public Hall[] hHalls;
    public Hall[] vHalls;

    public Room(double x1, double y1, double x2, double y2){
        // (x1, y1): top left corner position
        // (x2, y2): bottom right corner position
        dim = new Dimension(x1, y1, x2-x1, y2-y1);

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

        int nextType = type;
        if (Math.random() < .3) nextType++;

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

    private void shrink(double minSize) {
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
    }


    public void draw(){
        if (left != null) {
        left.draw();
        right.draw();
        } else {
            for (Hall h : hHalls) h.draw();
            for (Hall h : vHalls) h.draw();
        }
    }

    private double random(double a, double b){
        return Math.random()*(b-a)+a;
    }

}

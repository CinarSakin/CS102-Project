package com.game;

import java.util.ArrayList;

public class Hall {
    public Dimension dim;
    public ArrayList<Entity> entities;
    

    Hall(double x1, double y1, double x2, double y2) {
        dim = new Dimension(x1, y1, x2, y2);
        
    }

    void draw() {
        //TODO: draw the textures of the top walls then floor then entities inside and then lower walls
    }

    public double getX1(){return dim.getX();}
    public double getY1(){return dim.getY();}
    public double getX2(){return dim.getWidth() + dim.getX();}
    public double getY2(){return getHeight() + dim.getY();}
    public double getHeight(){return dim.getHeight();}
    public double getWidth(){return dim.getWidth();}

}

package com.game;

public class Hall extends Area {
    public boolean isHorizontal;

    Hall(double x1, double y1, double width, double height, boolean isHorizontal) {
        super(new Dimension(x1, y1, width, height));
        this.isHorizontal = isHorizontal;
    }

}

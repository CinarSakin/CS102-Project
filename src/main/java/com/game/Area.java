package com.game;

import java.util.ArrayList;

import javafx.scene.image.Image;

public abstract class Area {

    private static final Image image1 = new Image(Room.class.getResourceAsStream("/sprites/ui/wall.png"), Level.gridSize, 0, true, false);
    private static final Image image2 = new Image(Room.class.getResourceAsStream("/sprites/ui/wall_top.png"), Level.gridSize, 0, true, false);    
    private static final Image image3 = new Image(Room.class.getResourceAsStream("/sprites/ui/stone_floor.png"), Level.gridSize, 0, true, false);    


    protected Dimension dim;

    public ArrayList<Entity> entities = new ArrayList<Entity>();
    public transient ArrayList<LivingEntity> livingEntities = new ArrayList<LivingEntity>();
    public transient ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    public Area(Dimension dim) {
        this.dim = dim;
        this.dim.setArea(this);
    //    Level.getLevel().areas.add(this);
    }

    public Dimension getDimension() {return dim;}

    public static Image getImage(int i){
        //0 for top wall, 1 for right walls, 2 for ground, 3 for right walls, 4 for lower walls
        switch (i) {
            case 0://further wall
                return image1;
            case 1://left side wall
                return image2;
            case 2:// ground tiles
                return image3;
            case 3://right side wall
                return image2;
            case 4://closer wall
                return image2;
            default:
                throw new AssertionError();
        }
    }

    public void update(double dt) {
        for (Entity e : entities){
            e.update(dt);
        }
    }

    // Register Methods for Entities
    public void register(Entity e) {
        entities.add(e);
        if (e instanceof LivingEntity) livingEntities.add((LivingEntity) e);
        if (e instanceof Enemy) enemies.add((Enemy) e);
    }
    public void unregister(Entity e) {
        entities.remove(e);
        if (e instanceof LivingEntity) livingEntities.remove(e);
        if (e instanceof Enemy) enemies.remove(e);
    }

    // GETTERS
    public double getX1(){return dim.getX();}
    public double getY1(){return dim.getY();}
    public double getX2(){return dim.getWidth() + dim.getX();}
    public double getY2(){return getHeight() + dim.getY();}
    public double getHeight(){return dim.getHeight();}
    public double getWidth(){return dim.getWidth();}
    public ArrayList<Entity> getEntities() {return this.entities;}
    public ArrayList<LivingEntity> getLivingEntities() {return this.livingEntities;}
    public ArrayList<Enemy> getEnemies() {return this.enemies;}
    
}
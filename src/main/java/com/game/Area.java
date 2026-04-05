package com.game;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Area {

    protected Dimension dim;

    public ArrayList<Entity> entities = new ArrayList<Entity>();
    public ArrayList<LivingEntity> livingEntities = new ArrayList<LivingEntity>();
    public ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    public Area(Dimension dim) {
        this.dim = dim;
        this.dim.setArea(this);
        Level.getLevel().areas.add(this);
    }

    public Dimension getDimension() {return dim;}

    public void update(double dt) {
        for (Entity e : getEntities()){
            e.update(dt);
        }
    }

    public void draw() {
        entities.sort(Comparator.comparingDouble(e -> e.getDimension().getY()));
        for (Entity e : getEntities()){
            e.draw();
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
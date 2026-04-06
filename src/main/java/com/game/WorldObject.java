package com.game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public abstract class WorldObject extends Entity {
    
    public boolean interacted;
    public double interactRadius;

    public WorldObject(Point2D position, double width, double height, Area currentArea, double interactRadius) {
        super(new Dimension(position.getX(), position.getY(), width*Level.gridSize, height*Level.gridSize), currentArea);
        this.interactRadius = interactRadius;
        this.interacted = false;
    }

    public abstract void interact();

    public boolean isHeroInRange() {        
        double distance = dimension.getCenter().distance(Hero.getHero().dimension.getCenter());
        return distance <= interactRadius;
    }

    @Override
    public void update(double dt) {}
}

class Chest extends WorldObject {
    
    public Item item;
    public boolean open;
    public boolean unlocked;

    public Chest(Point2D position, Area currentArea, Item item) {
        super(position, 1, 1, currentArea, Level.gridSize*3);
        this.item = item;
        this.open = false;
        this.unlocked = false;
        this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/world/chest_closed.png"), Level.gridSize, 0, true, false);
        this.dimension.moveCenterTo(position.getX(), position.getY()-Level.gridSize/2);
    }

    @Override
    public void interact() {
        if (!isHeroInRange() || open) return;
        this.open = true;
        this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/world/chest_open.png"), Level.gridSize, 0, true, false);
    }

    @Override
    public void reloadImages() {
        this.imageToDraw = open
            ? new Image(getClass().getResourceAsStream("/sprites/world/chest_open.png"), Level.gridSize, 0, true, false)
            : new Image(getClass().getResourceAsStream("/sprites/world/chest_closed.png"), Level.gridSize, 0, true, false);
    }
}

class DroppedItem extends WorldObject {
    
    public Item item;

    public DroppedItem(Point2D position, Area currentArea, Item item) {
        super(position, .8, .8 , currentArea, Level.gridSize);
        this.item = item;
        this.imageToDraw = item.image;
    }

    public void interact() {
        if (!isHeroInRange()) return;
        // add item to inventory
        this.despawn(); 
    }
}

class Gate extends WorldObject {

    public Gate(Point2D position, Area currentArea) {
        super(position, 3, 1, currentArea, Level.gridSize*3);
        this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/world/gate.png"));
    }

    @Override
    public void interact() {
        if (!isHeroInRange()) return;

    }

    @Override
    public void reloadImages() {
        this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/world/gate.png"));
    }

}

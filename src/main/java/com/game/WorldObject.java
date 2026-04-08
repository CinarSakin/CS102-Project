package com.game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public abstract class WorldObject extends Entity {
    
    public boolean interacted;
    public double interactRadius;

    public WorldObject(Point2D position, double width, double height, Area currentArea, double interactRadius) {
        super(
            new Dimension(position.getX()-width*Level.gridSize/2, position.getY()-height*Level.gridSize/2,
            width*Level.gridSize, height*Level.gridSize), currentArea
        );
        this.interactRadius = interactRadius;
        this.interacted = false;
    }

    public abstract boolean interact();

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
        super(position, 1, 1, currentArea, Level.gridSize*4);
        this.item = item;
        this.open = false;
        this.unlocked = false;
        this.dimension.moveCenterTo(position.getX(), position.getY()-Level.gridSize/2);
        reloadImages();
    }

    @Override
    public boolean interact() {
        if (!isHeroInRange() || open) return false;
        this.open = true;
        reloadImages();
        new DroppedItem(this.dimension.getCenter().add(new Point2D(0, Level.gridSize)), currentArea, item);
        return true;
    }

    @Override
    public void reloadImages() {
        String path = open ? "/sprites/world/chest_open.png" : "/sprites/world/chest_closed.png";
        double w = dimension.getWidth(), h = dimension.getHeight();
        this.imageToDraw = new Image(getClass().getResourceAsStream(path), w, h, false, false);
    }
}

class DroppedItem extends WorldObject {

    public static final double DROP_SIZE = 0.8 * Level.gridSize;
    public Item item;

    public DroppedItem(Point2D position, Area currentArea, Item item) {
        super(position, .8, .8, currentArea, Level.gridSize*2);
        this.item = item;
        reloadImages();
    }

    @Override
    public void update(double dt) {
        if (imageToDraw == null) reloadImages();
    }

    @Override
    public void reloadImages() {
        this.imageToDraw = item.loadImageAtSize(dimension.getWidth());
    }

    public boolean interact() {
        if (!isHeroInRange()) return false;
        if (Hero.getHero().addItem(this.item)) {
            this.despawn();
            return true;
        }
        return false;
    }
}

class Gate extends WorldObject {

    public Gate(Point2D position, Area currentArea) {
        super(position, 3, 1, currentArea, Level.gridSize*3);
        this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/world/gate.png"), dimension.getWidth(), dimension.getHeight(), false, false);
    }

    @Override
    public boolean interact() {
        if (!isHeroInRange()) return false;
        return false;
    }

    @Override
    public void reloadImages() {
        this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/world/gate.png"), dimension.getWidth(), dimension.getHeight(), false, false);
    }

}

class Portal extends WorldObject {
    
    public Portal(Point2D position, Area currentArea) {
        super(position, 1.5, 1.83, currentArea, Level.gridSize*3);
        reloadImages();
    }

    @Override
    public boolean interact() {
        if (!isHeroInRange()) return false;
        Level.endLevel();
        return true;
    }

    @Override
    public void reloadImages() {
        this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/world/portal.png"), dimension.getWidth(), dimension.getHeight(), false, false);
    }
}

package com.game;

import java.time.LocalTime;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public abstract class WorldObject extends Entity {
    
    public boolean interactable = true;
    public double interactRadius;
    public String actionText;
    public String info;

    public WorldObject(Point2D position, double width, double height, Area currentArea, double interactRadius, String actionText, String info) {
        super(
            new Dimension(position.getX()-width*Level.gridSize/2, position.getY()-height*Level.gridSize/2,
            width*Level.gridSize, height*Level.gridSize), currentArea
        );
        this.interactRadius = interactRadius;
        this.actionText = actionText;
        this.info = info;
    }

    public abstract boolean interact();

    public boolean isHeroInRange() {        
        double distance = dimension.getCenter().distance(Hero.getHero().dimension.getCenter());
        return distance <= interactRadius;
    }

    @Override
    public void update(double dt) {
    }
}

class Chest extends WorldObject {
    
    public Item item;
    public boolean open;
    public boolean unlocked;

    public Chest(Point2D position, Area currentArea, Item item) {
        super(position, 1, 1, currentArea, Level.gridSize*4, "INTERACT", "Mystery Chest");
        this.item = item;
        this.open = false;
        this.unlocked = false;
        this.dimension.moveCenterTo(position.getX(), position.getY()-Level.gridSize/2);
        reloadImages();
    }

    @Override
    public boolean interact() {
        if (!isHeroInRange() || !interactable) return false;
        GameStats.getInstance().chestsOpened++;
        this.open = true;
        this.interactable = false;
        reloadImages();
        new DroppedItem(this.dimension.getCenter().add(0, Level.gridSize), currentArea, item);
        return true;
    }

    @Override
    public void reloadImages() {
        String path = open ? "/sprites/world/chest_open.png" : "/sprites/world/chest_closed.png";
        double w = dimension.getWidth(), h = dimension.getHeight();
        this.imageToDraw = new Image(getClass().getResourceAsStream(path), w, h, false, false);
    }
}

class Plate extends WorldObject {

    public Item item;
    public transient boolean pressed;
    public transient LocalTime pressTime;

    public Plate(Point2D position, Area currentArea, Item item, int index) {
        super(position, 1, 1, currentArea, Level.gridSize*4, "INTERACT", ("'" + index + "'"));
        this.item = item;
        this.pressed = false;
        this.dimension.moveCenterTo(position.getX(), position.getY()-Level.gridSize/2);
        reloadImages();
    }

    @Override
    public boolean interact() {
        if (!isHeroInRange() || !interactable) return false;
        pressed = !pressed;
        if (pressed) pressTime = LocalTime.now();
        this.interactable = false;
        reloadImages();
        return true;
    }

    public boolean getPressed(){return pressed;}

    @Override
    public void reloadImages() {
        String path = pressed ? "/sprites/world/plate_pressed.png" : "/sprites/world/plate.png";
        double w = dimension.getWidth(), h = dimension.getHeight();
        this.imageToDraw = new Image(getClass().getResourceAsStream(path), w, h, false, false);
    }
}

class DroppedItem extends WorldObject {

    public static final double DROP_SIZE = 0.8 * Level.gridSize;
    public Item item;

    public DroppedItem(Point2D position, Area currentArea, Item item) {
        super(position, .8, .8, currentArea, Level.gridSize*2, "GATHER ITEM", item.name);
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
        super(position, 3, 1, currentArea, Level.gridSize*3, "INTERACT", "Gate");
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
        super(position.subtract(0, Level.gridSize*1.8), 2.4, 3, currentArea, Level.gridSize*3, "GO THROUGH", "Portal");
        reloadImages();
    }

    @Override
    public boolean interact() {
        if (!isHeroInRange()) return false;
        GameStats.getInstance().levelsCleared++;
        Level.endLevel();
        return true;
    }

    @Override
    public void reloadImages() {
        this.imageToDraw = new Image(getClass().getResourceAsStream("/sprites/world/portal.png"), dimension.getWidth(), dimension.getHeight(), false, false);
    }
}

package com.game;

import javafx.scene.image.Image;

public class Consumable extends Item {

    public Image getImage() {
        return new Image(Hero.class.getResourceAsStream("/sprites/items/talismans/talisman-1.png"),
        Level.gridSize, 0, true, false);
    }
    
    public Consumable(Image image, String name, String description, double cooldownDuration) {
        super(image, name, description, cooldownDuration);
    }
}

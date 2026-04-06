package com.game;

import javafx.scene.image.Image;

public class Consumable extends Item {
    
    public Consumable(Image image, String name, String description, double cooldownDuration) {
        super(image, name, description, cooldownDuration);
    }
}
